import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { initialOrders } from "../shared/data/mockData";
import { loadJson, saveJson } from "../shared/lib/storage";
import type { CartItem, Language, Order, ShipContext } from "../shared/types/domain";

interface AppStateValue {
  language: Language;
  setLanguage: (language: Language) => void;
  currentShip: ShipContext | null;
  setCurrentShip: (ship: ShipContext | null) => void;
  cartItems: CartItem[];
  addToCart: (productId: string) => void;
  updateCartQuantity: (productId: string, quantity: number) => void;
  removeFromCart: (productId: string) => void;
  clearCart: () => void;
  orders: Order[];
  saveOrder: (order: Order) => void;
  updateOrder: (order: Order) => void;
}

const AppStateContext = createContext<AppStateValue | null>(null);

export function AppStateProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>(() => loadJson<Language>("tqh.language", "zh"));
  const [currentShip, setCurrentShipState] = useState<ShipContext | null>(() =>
    loadJson<ShipContext | null>("tqh.currentShip", null)
  );
  const [cartItems, setCartItems] = useState<CartItem[]>(() => loadJson<CartItem[]>("tqh.cart", []));
  const [orders, setOrders] = useState<Order[]>(() => loadJson<Order[]>("tqh.orders", initialOrders));

  const setLanguage = useCallback((next: Language) => {
    setLanguageState(next);
    saveJson("tqh.language", next);
  }, []);

  const setCurrentShip = useCallback((ship: ShipContext | null) => {
    setCurrentShipState(ship);
    saveJson("tqh.currentShip", ship);
  }, []);

  const persistCart = useCallback((updater: (items: CartItem[]) => CartItem[]) => {
    setCartItems((current) => {
      const next = updater(current);
      saveJson("tqh.cart", next);
      return next;
    });
  }, []);

  const addToCart = useCallback(
    (productId: string) =>
      persistCart((current) => {
        const existing = current.find((item) => item.productId === productId);

        if (!existing) {
          return [...current, { productId, quantity: 1 }];
        }

        return current.map((item) => (item.productId === productId ? { ...item, quantity: item.quantity + 1 } : item));
      }),
    [persistCart]
  );

  const updateCartQuantity = useCallback(
    (productId: string, quantity: number) =>
      persistCart((current) =>
        current.map((item) => (item.productId === productId ? { ...item, quantity } : item)).filter((item) => item.quantity > 0)
      ),
    [persistCart]
  );

  const removeFromCart = useCallback(
    (productId: string) => persistCart((current) => current.filter((item) => item.productId !== productId)),
    [persistCart]
  );

  const clearCart = useCallback(() => persistCart(() => []), [persistCart]);

  const persistOrders = useCallback((updater: (orders: Order[]) => Order[]) => {
    setOrders((current) => {
      const next = updater(current);
      saveJson("tqh.orders", next);
      return next;
    });
  }, []);

  const saveOrder = useCallback((order: Order) => persistOrders((current) => [order, ...current]), [persistOrders]);

  const updateOrder = useCallback(
    (order: Order) => persistOrders((current) => current.map((candidate) => (candidate.id === order.id ? order : candidate))),
    [persistOrders]
  );

  const value = useMemo<AppStateValue>(
    () => ({
      language,
      setLanguage,
      currentShip,
      setCurrentShip,
      cartItems,
      addToCart,
      updateCartQuantity,
      removeFromCart,
      clearCart,
      orders,
      saveOrder,
      updateOrder
    }),
    [
      addToCart,
      cartItems,
      clearCart,
      currentShip,
      language,
      orders,
      removeFromCart,
      saveOrder,
      setCurrentShip,
      setLanguage,
      updateCartQuantity,
      updateOrder
    ]
  );

  return <AppStateContext.Provider value={value}>{children}</AppStateContext.Provider>;
}

export function useAppState(): AppStateValue {
  const value = useContext(AppStateContext);

  if (!value) {
    throw new Error("useAppState must be used inside AppStateProvider");
  }

  return value;
}
