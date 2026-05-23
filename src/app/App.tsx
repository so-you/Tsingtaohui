import { useEffect, useState } from "react";
import { CatalogPage } from "../features/catalog/CatalogPage";
import { ProductDetailPage } from "../features/catalog/ProductDetailPage";
import { OrderConfirmPage } from "../features/cart/OrderConfirmPage";
import { HomePage } from "../features/home/HomePage";
import { OrderDetailPage } from "../features/orders/OrderDetailPage";
import { OrdersPage } from "../features/orders/OrdersPage";
import { ProfilePage } from "../features/profile/ProfilePage";
import { ScanPage } from "../features/scan/ScanPage";
import { InvalidShipTokenPage } from "../features/ship/InvalidShipTokenPage";
import { resolveShipToken } from "../features/ship/shipLogic";
import { I18nProvider } from "../shared/i18n/I18nProvider";
import { AppLayout } from "./AppLayout";
import { AppStateProvider, useAppState } from "./AppState";
import type { AppRoute } from "./routes";

function AppInner() {
  const [route, setRoute] = useState<AppRoute>("home");
  const [selectedProductId, setSelectedProductId] = useState("prod-water");
  const [selectedOrderId, setSelectedOrderId] = useState<string | null>(null);
  const { currentShip, orders, setCurrentShip } = useAppState();

  useEffect(() => {
    if (currentShip) {
      return;
    }

    const token = new URLSearchParams(window.location.search).get("ship_token") ?? undefined;
    const resolution = resolveShipToken(token);

    if (resolution.status === "valid") {
      setCurrentShip(resolution.ship);
    } else {
      setRoute("invalidShip");
    }
  }, [currentShip, setCurrentShip]);

  function openProduct(productId: string) {
    setSelectedProductId(productId);
    setRoute("productDetail");
  }

  function openOrder(orderId: string) {
    setSelectedOrderId(orderId);
    setRoute("orderDetail");
  }

  function renderRoute() {
    switch (route) {
      case "home":
        return <HomePage onNavigate={setRoute} />;
      case "goods":
        return <CatalogPage onOpenProduct={openProduct} onOpenCart={() => setRoute("cart")} />;
      case "productDetail":
        return <ProductDetailPage productId={selectedProductId} onBack={() => setRoute("goods")} onOpenCart={() => setRoute("cart")} />;
      case "invalidShip":
        return (
          <InvalidShipTokenPage
            onUseDemo={() => {
              const resolution = resolveShipToken(undefined);
              if (resolution.status === "valid") {
                setCurrentShip(resolution.ship);
                setRoute("home");
              }
            }}
          />
        );
      case "cart":
        return (
          <OrderConfirmPage
            onOrderCreated={(orderId) => {
              setSelectedOrderId(orderId);
              setRoute("orderDetail");
            }}
          />
        );
      case "orders":
        return <OrdersPage onOpenOrder={openOrder} />;
      case "scan":
        return <ScanPage onOpenOrder={openOrder} onOpenProduct={openProduct} />;
      case "mine":
        return <ProfilePage />;
      case "orderDetail":
        return <OrderDetailPage orderId={selectedOrderId ?? orders[0]?.id ?? ""} onBack={() => setRoute("orders")} />;
      default:
        return null;
    }
  }

  return (
    <AppLayout activeRoute={route} onNavigate={setRoute}>
      {renderRoute()}
    </AppLayout>
  );
}

export function App() {
  return (
    <AppStateProvider>
      <I18nProvider>
        <AppInner />
      </I18nProvider>
    </AppStateProvider>
  );
}
