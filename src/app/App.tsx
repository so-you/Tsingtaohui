import { useEffect, useState } from "react";
import { CatalogPage } from "../features/catalog/CatalogPage";
import { ProductDetailPage } from "../features/catalog/ProductDetailPage";
import { HomePage } from "../features/home/HomePage";
import { InvalidShipTokenPage } from "../features/ship/InvalidShipTokenPage";
import { resolveShipToken } from "../features/ship/shipLogic";
import { I18nProvider } from "../shared/i18n/I18nProvider";
import { AppLayout } from "./AppLayout";
import { AppStateProvider, useAppState } from "./AppState";
import type { AppRoute } from "./routes";

function AppInner() {
  const [route, setRoute] = useState<AppRoute>("home");
  const [selectedProductId, setSelectedProductId] = useState("prod-water");
  const { currentShip, setCurrentShip } = useAppState();

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
          <section className="page">
            <h1>订单确认</h1>
            <p>Cart and order confirmation are implemented in the next task.</p>
          </section>
        );
      case "orders":
        return (
          <section className="page">
            <h1>订单</h1>
            <p>Order tracking is implemented in a later task.</p>
          </section>
        );
      case "scan":
        return (
          <section className="page">
            <h1>扫码模拟</h1>
            <p>Scan actions are implemented in a later task.</p>
          </section>
        );
      case "mine":
        return (
          <section className="page">
            <h1>我的</h1>
            <p>Profile settings are implemented in a later task.</p>
          </section>
        );
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
