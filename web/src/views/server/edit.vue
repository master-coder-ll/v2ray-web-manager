<template>
  <server-detail :is-edit="true" />

</template>

<script>
import ServerDetail from './components/ServerDetail'

export default {
  name: 'EditForm',
  components: { ServerDetail }
}
</script>

