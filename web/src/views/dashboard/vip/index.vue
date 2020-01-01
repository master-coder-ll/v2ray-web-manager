<template>
  <div class="dashboard-editor-container">
    <!-- <el-row>
      <el-col :xs="24" :sm="24" :lg="12">
        <pan-thumb :image="avatar" style="float: left">
          Your roles:
          <span v-for="item in roles" :key="item" class="pan-info-roles">{{ item }}</span>
        </pan-thumb>
      </el-col  >
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="text-align:center">  <span class=""> Your name:  {{ name }}</span></div>    
      
        </el-col>

    </el-row> -->

    <!-- <github-corner style="position: absolute; top: 0px; border: 0; right: 0;" /> -->
  <el-row>
    <h3>公告</h3>
  </el-row>
    <el-row>
      <el-collapse v-model="activeNames">
      
        <div v-for=" (item,index) in list" :key="index">
      <el-collapse-item :title="item.name" :name="index" >
       <div v-html="item.content" />
      </el-collapse-item>
      </div>
   
  
  
</el-collapse>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PanThumb from '@/components/PanThumb'
// import GithubCorner from '@/components/GithubCorner'
import { listNotice } from '@/api/notice' 

export default {
  name: 'DashboardEditor',
  components: { PanThumb },
  data() {
    return {
      activeNames: 0,
      list:null
    }
  },
  created(){
      listNotice().then(response =>{
           this.list= response.obj
           this.activeNames=0;
      })
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles'
    ])
  }
}
</script>

<style lang="scss" >


 

  .dashboard-editor-container {
   // background-color: #e3e3e3;
     a, a:focus, a:hover {
    cursor: pointer;
    color: #409EFF;
    text-decoration: none;
}
    min-height: 100vh;
    padding: 50px 60px 0px;
    .pan-info-roles {
      font-size: 12px;
      font-weight: 700;
      color: #333;
      display: block;
    }
   
  

  }
</style>
