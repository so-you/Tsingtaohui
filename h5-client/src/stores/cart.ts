import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { ICartItem, IProduct } from '../types'

export const useCartStore = defineStore(
  'cart',
  () => {
    const items = ref<ICartItem[]>([])
    const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

    function addProduct(product: IProduct, quantity = 1) {
      const existing = items.value.find((item) => item.productId === product.id)
      if (existing) {
        existing.quantity += quantity
        return
      }
      items.value.push({
        productId: product.id,
        skuCode: product.skuCode,
        nameZh: product.nameZh,
        nameEn: product.nameEn,
        price: product.price,
        quantity,
        weightKg: product.weightKg,
        volumeM3: product.volumeM3,
        mainImageUrl: product.mainImageUrl,
      })
    }

    function updateQuantity(productId: number, quantity: number) {
      const item = items.value.find((entry) => entry.productId === productId)
      if (!item) return
      item.quantity = Math.max(1, quantity)
    }

    function removeItem(productId: number) {
      items.value = items.value.filter((item) => item.productId !== productId)
    }

    function clear() {
      items.value = []
    }

    return {
      items,
      totalQuantity,
      addProduct,
      updateQuantity,
      removeItem,
      clear,
    }
  },
  {
    persist: {
      key: 'cart',
      storage: {
        getItem: (key: string) => {
          try {
            return localStorage.getItem(key)
          } catch {
            return null
          }
        },
        setItem: (key: string, value: string) => {
          try {
            localStorage.setItem(key, value)
          } catch {
            // ignore storage errors
          }
        },
      },
      paths: ['items'],
    },
  },
)
