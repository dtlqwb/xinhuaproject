import { defineAppConfig } from '@tarojs/taro'

export default defineAppConfig({
  pages: [
    'pages/login/index',
    'pages/index/index'
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#667eea',
    navigationBarTitleText: '智销助手',
    navigationBarTextStyle: 'white'
  },
  tabBar: {
    color: '#999',
    selectedColor: '#667eea',
    backgroundColor: '#fff',
    list: [
      {
        pagePath: 'pages/index/index',
        text: '客户录入',
        iconPath: 'assets/icons/home.png',
        selectedIconPath: 'assets/icons/home-active.png'
      }
    ]
  }
})
