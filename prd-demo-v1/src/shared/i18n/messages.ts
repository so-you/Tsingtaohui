import type { Language } from "../types/domain";

export const messages = {
  zh: {
    "tabs.home": "首页",
    "tabs.goods": "商品",
    "tabs.orders": "订单",
    "tabs.scan": "扫码",
    "tabs.mine": "我的",
    "common.confirm": "确认",
    "common.cancel": "取消",
    "common.save": "保存",
    "common.refresh": "刷新状态",
    "home.activeOrders": "进行中订单",
    "home.availableSkus": "可选商品",
    "catalog.search": "搜索商品",
    "catalog.addToCart": "加入购物车",
    "catalog.buyNow": "立即下单",
    "cart.title": "订单确认",
    "cart.submit": "提交订单",
    "orders.title": "订单",
    "orders.detail": "订单详情",
    "orders.confirmReceipt": "确认收货",
    "scan.title": "扫码模拟",
    "profile.language": "语言"
  },
  en: {
    "tabs.home": "Home",
    "tabs.goods": "Goods",
    "tabs.orders": "Orders",
    "tabs.scan": "Scan",
    "tabs.mine": "Mine",
    "common.confirm": "Confirm",
    "common.cancel": "Cancel",
    "common.save": "Save",
    "common.refresh": "Refresh Status",
    "home.activeOrders": "Active Orders",
    "home.availableSkus": "Available SKUs",
    "catalog.search": "Search goods",
    "catalog.addToCart": "Add to cart",
    "catalog.buyNow": "Buy now",
    "cart.title": "Order Confirmation",
    "cart.submit": "Submit Order",
    "orders.title": "Orders",
    "orders.detail": "Order Detail",
    "orders.confirmReceipt": "Confirm Receipt",
    "scan.title": "Scan Demo",
    "profile.language": "Language"
  }
} as const;

export type MessageKey = keyof typeof messages.zh;

export function getMessage(language: Language, key: MessageKey): string {
  return messages[language][key];
}
