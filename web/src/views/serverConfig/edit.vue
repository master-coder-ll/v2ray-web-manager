<template>
  <ServerConfig-detail :is-edit="true" />

</template>

<script>
import ServerConfigDetail from './components/ServerConfigDetail'

export default {
  name: 'CreateServerConfigDetail',
  components: { ServerConfigDetail }
}
</script>

