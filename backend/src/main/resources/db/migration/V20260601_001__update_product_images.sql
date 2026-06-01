-- V20260601_001: Add product images for display
UPDATE t_product SET main_image_url = '/files/products/cola.png'    WHERE sku_code = 'SKU-COKE-330';
UPDATE t_product SET main_image_url = '/files/products/water.png'   WHERE sku_code = 'SKU-WATER-550';
UPDATE t_product SET main_image_url = '/files/products/coffee.png'  WHERE sku_code = 'SKU-COFFEE-200';
UPDATE t_product SET main_image_url = '/files/products/biscuit.png' WHERE sku_code = 'SKU-BISCUIT-120';
UPDATE t_product SET main_image_url = '/files/products/tea.png'     WHERE sku_code = 'SKU-TEA-500';
UPDATE t_product SET main_image_url = '/files/products/noodle.png'  WHERE sku_code = 'SKU-NOODLE-110';
