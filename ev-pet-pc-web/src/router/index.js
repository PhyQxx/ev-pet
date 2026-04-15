import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue')
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('../views/chat/Chat.vue')
      },
      {
        path: 'growth',
        name: 'Growth',
        component: () => import('../views/growth/Growth.vue')
      },
      {
        path: 'studio',
        name: 'Studio',
        component: () => import('../views/studio/Studio.vue')
      },
      {
        path: 'shop',
        name: 'Shop',
        component: () => import('../views/shop/Shop.vue')
      },
      {
        path: 'achievement',
        name: 'Achievement',
        component: () => import('../views/achievement/Achievement.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/settings/Settings.vue')
      },
      {
        path: 'work',
        name: 'Work',
        component: () => import('../views/work/Work.vue')
      },
      {
        path: 'social',
        name: 'Social',
        component: () => import('../views/social/Social.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
