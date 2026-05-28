import type { Product } from "../../shared/types/domain";

export interface ProductFilter {
  categoryId?: string;
  keyword?: string;
}

export function filterProducts(products: Product[], filter: ProductFilter): Product[] {
  const keyword = filter.keyword?.trim().toLocaleLowerCase();

  return products.filter((product) => {
    const matchesCategory = !filter.categoryId || product.categoryId === filter.categoryId;
    const matchesKeyword =
      !keyword ||
      [
        product.nameZh,
        product.nameEn,
        product.descriptionZh,
        product.descriptionEn,
        product.skuCode,
        product.specification
      ]
        .join(" ")
        .toLocaleLowerCase()
        .includes(keyword);

    return matchesCategory && matchesKeyword;
  });
}
