<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 64 64"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <mask id="ccb-hole">
      <rect width="64" height="64" fill="#fff" />
      <!-- 中心方孔（古钱币方孔，旋转呈菱形） -->
      <rect x="24" y="24" width="16" height="16" transform="rotate(45 32 32)" fill="#000" />
      <!-- 右上楔形缺口 -->
      <path d="M38.5 20.7 L47 6 A30 30 0 0 1 61.9 29.4 L45 30.9 A13 13 0 0 0 38.5 20.7 Z" fill="#000" />
    </mask>
    <!-- 实心圆盘：方孔镂空 + 右上缺口 -->
    <circle cx="32" cy="32" r="26" fill="currentColor" mask="url(#ccb-hole)" />
    <!-- 右上斜方块 -->
    <rect x="47.2" y="13.3" width="11" height="11" transform="rotate(45 52.7 18.8)" fill="currentColor" />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 32 },
})
</script>
