import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('../components/Layout.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/dashboard/Dashboard.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/users/Users.vue') },
      { path: 'content', name: 'Content', component: () => import('../views/content/Content.vue') },
      { path: 'items', name: 'Items', component: () => import('../views/items/Items.vue') },
      { path: 'announcements', name: 'Announcements', component: () => import('../views/announcements/Announcements.vue') },
      { path: 'activities', name: 'Activities', component: () => import('../views/activities/Activities.vue') },
      { path: 'achievements', name: 'Achievements', component: () => import('../views/achievements/Achievements.vue') },
      { path: 'report', name: 'Report', component: () => import('../views/report/Report.vue') },
      { path: 'pets', name: 'Pets', component: () => import('../views/pets/Pets.vue') },
      { path: 'permissions', name: 'Permissions', component: () => import('../views/permissions/Permissions.vue') },
      { path: 'settings', name: 'Settings', component: () => import('../views/settings/Settings.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
