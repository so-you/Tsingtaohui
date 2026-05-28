import { useMemo, useState } from "react";
import { useAppState } from "../../app/AppState";
import { Button } from "../../shared/components/Button";
import { categories, products } from "../../shared/data/mockData";
import { useI18n } from "../../shared/i18n/I18nProvider";
import { filterProducts } from "./catalogLogic";
import { ProductCard } from "./ProductCard";

export interface CatalogPageProps {
  onOpenCart: () => void;
  onOpenProduct: (productId: string) => void;
}

export function CatalogPage({ onOpenProduct, onOpenCart }: CatalogPageProps) {
  const { cartItems, addToCart, language } = useAppState();
  const { t } = useI18n();
  const [categoryId, setCategoryId] = useState<string | undefined>();
  const [keyword, setKeyword] = useState("");

  const filteredProducts = useMemo(() => filterProducts(products, { categoryId, keyword }), [categoryId, keyword]);
  const visibleCategories = categories.filter((category) => category.parentId);

  return (
    <section className="page catalog-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Bonded Warehouse</p>
          <h1>{t("tabs.goods")}</h1>
        </div>
        <Button onClick={onOpenCart} variant="secondary">
          购物车 {cartItems.length}
        </Button>
      </header>

      <input
        className="search-input"
        onChange={(event) => setKeyword(event.target.value)}
        placeholder={t("catalog.search")}
        type="search"
        value={keyword}
      />

      <div className="category-row" role="list" aria-label="Product categories">
        <button className={!categoryId ? "category-chip category-chip-active" : "category-chip"} type="button" onClick={() => setCategoryId(undefined)}>
          All
        </button>
        {visibleCategories.map((category) => (
          <button
            className={categoryId === category.id ? "category-chip category-chip-active" : "category-chip"}
            key={category.id}
            type="button"
            onClick={() => setCategoryId(category.id)}
          >
            {language === "zh" ? category.nameZh : category.nameEn}
          </button>
        ))}
      </div>

      <div className="product-list">
        {filteredProducts.map((product) => (
          <ProductCard key={product.id} language={language} product={product} onAddToCart={addToCart} onOpen={onOpenProduct} />
        ))}
      </div>
    </section>
  );
}
