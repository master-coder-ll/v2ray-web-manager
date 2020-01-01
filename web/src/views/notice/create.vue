<template>
  <notice-detail :is-edit="false" />
</template>

<script>
import NoticeDetail from './components/NoticeDetail'

export default {
  name: 'CreateNotice',
  components: { NoticeDetail }
}
</script>

