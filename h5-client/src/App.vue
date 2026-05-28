<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from './stores/user'

onLaunch(() => {
  const userStore = useUserStore()
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.fetchProfile().catch(() => {
      // Profile fetch failed, token may be expired
    })
  }
})

onShow(() => {
  // App shown
})

onHide(() => {
  // App hidden
})
</script>

<style>
@import './styles/common.scss';
</style>
