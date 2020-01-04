<template>
  <server-detail :is-edit="false" />
</template>

<script>
import ServerDetail from './components/ServerDetail'

export default {
  name: 'CreateServer',
  components: { ServerDetail }
}
</script>

