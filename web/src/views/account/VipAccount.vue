<template>
  <div class="dashboard-editor-container">
    <el-form size="mini">
    <el-row v-for=" (row,index) in list" :key="index" :gutter="20">
      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
        <div slot="header" >
          
       账号: {{ row.accountNo }}  
         <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
       </div>
          

          <el-form-item label=" 有效时间:" >
              <!--  {{ row.fromDate | parseTime('{y}-{m}-{d} {h}:{i}') }} - -->
        
            <span>
              <font v-if="row.toDate>new Date().getTime()">  {{ row.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
              <font v-else color="red">  {{ row.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
            </span>
          </el-form-item>

            <el-form-item label=" 结算时间:" v-if="row.stat">
              <span>{{row.stat.toDate  | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
            </el-form-item>
          <el-form-item label="速率:" >
            <span></span>
            <span>{{ row.speed | speedFilter }}</span>
            </el-form-item>
          <el-form-item label="周期:" >
            <!-- <span>周期：</span> -->
            {{ row.cycle }}天/周期
            </el-form-item>
          <el-form-item label="流量:" >
            
       
            <span>
              <font v-if="(row.stat?(row.stat.flow/1024/1024/1024).toFixed(2) : 0)<row.bandwidth">{{ row.stat?(row.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
              <font v-else color="red">{{ row.stat?(row.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
              /{{ row.bandwidth }}GB/周期</span>
          </el-form-item>
          <el-form-item label="连接数:">{{connections}} /{{ row.maxConnection }}/账号
              <el-link icon="el-icon-view" type="primary" @click="getConnection(row,row.id)" v-show="row.server" >获取连接数</el-link>
          </el-form-item>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
          <div slot="header" >
          服务器信息
          </div>
          <div v-if="row.server">
           <el-form-item label="服务器名称:">   {{ row.server.serverName  }}  </el-form-item>
            <el-form-item label="服务器地址:">{{ row.server.clientDomain }}</el-form-item>
            <el-form-item label="流量倍数:">{{ row.server.multiple }}</el-form-item>
            <el-form-item label="服务器状态:">{{ row.server.status |statusFilter2 }}</el-form-item>
            <div> <el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(row.id)">更改服务器</el-link> </div>
          </div>
          <div v-else><el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(row.id)">选择你的服务器</el-link> </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
            <div slot="header" >
            v2ray账号
          </div>
          <div v-if="row.content.add">
            <el-row >
             <el-col :xs="24" :sm="24" :lg="12">
            <el-form-item label="地址:">{{ row.content.add }}</el-form-item>
            <el-form-item label="端口:">{{ row.content.port }}</el-form-item>
            <el-form-item label="用户Id:">{{ row.content.id }}</el-form-item>
            <el-form-item label="额外Id(alterId):">{{ row.content.aid }}</el-form-item>
            <el-form-item label="加密方式:">auto</el-form-item>
            <el-form-item label="传输协议:">{{ row.content.net }}</el-form-item>
            <el-form-item label="伪装类型:">{{ row.content.type }}</el-form-item>
            <el-form-item label="传输域名(host):">{{ row.content.host }}</el-form-item>
            <el-form-item label="路径(path):">{{ row.content.path }}</el-form-item>
            <el-form-item label="底层传输安全(tls):">{{ row.content.tls }}</el-form-item>
               </el-col> 
              <el-col :xs="24" :sm="24" :lg="12" >
              <el-form-item label="" >
              <el-col :xs="24" :sm="24" :lg="6">
                   <el-input v-model="row.toColip"> <el-button slot="append" @click="handlerCopy(row.toColip,$event)">复制</el-button> </el-input>
              </el-col>
            </el-form-item>
            <el-form-item label="">
              <vue-qr :text="row.toColip" qid="qrcode" />
            </el-form-item>

              </el-col>
            </el-row>
           
         
          </div>
        </el-card>
      </el-col>
  

    </el-row>
    <!-- <el-table v-loading="listLoading" :data="list"  fit highlight-current-row style="width: 100%"> -->

    <!-- 服务器列表 -->
    <el-row>
      <el-col :xs="24" :sm="24" :lg="12">

        <el-dialog title="服务器列表" :width="dialogWidth" :visible.sync="serverDialog" :before-close="handlerServerCloseDialog">

          <el-table ref="multipleTable" :data="serverList" fit highlight-current-row style="width: 100%" @selection-change="handleCurrentChange">

            <el-table-column
              type="selection"
            />

            <el-table-column align="center" label="服务器">
              <template slot-scope="scope">
                <el-col :span="24">
                  <span>{{ scope.row.serverName }}</span>
                </el-col>
                <el-col :span="24">
                  <span>{{ scope.row.clientDomain }}</span>
                </el-col>

              </template>
            </el-table-column>

            <el-table-column align="center" label="描述">
              <template slot-scope="scope">
                <span>{{ scope.row.desc }}</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="状态">
              <template slot-scope="{row}">
                <el-tag :type="row.status | statusFilter">
                  {{ row.status |statusFilter2 }}
                </el-tag>

              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 20px">
            <el-button @click="submitUpdateServer">提交</el-button>
          </div>
          <pagination v-show="total>0" :total="serverTotal" :page.sync="serverListQuery.page" :limit.sync="serverListQuery.pageSize" @pagination="getServerList" />
        </el-dialog>
      </el-col>

    </el-row>
    </el-form>
  </div>
</template>
<script>
import { getAccounts, accountsList, updateAccountServer, updateAccount,getConnection } from '@/api/account'
import { serverList } from '@/api/server'
import Pagination from '@/components/Pagination'
import clip from '@/utils/clipboard'
import { Base64 } from 'js-base64'
import VueQr from 'vue-qr'
import store from '@/store'
import { log } from 'util'
import permission from '@/directive/permission/index.js'

var oneDayms = 3600 * 1000 * 24
export default {
  name: 'UserAccount',
  components: { Pagination, VueQr },
  directives: { permission },
  filters: {
    speedFilter: function(v) {
      if (v <= 1024) { return '流畅' } else if (v > 1024 && v <= 2024) {
        return '高速'
      } else {
        return '极速'
      }
    },
    statusFilter(status) {
      const statusMap = {
        '1': 'success',
        '0': 'danger'
      }
      return statusMap[status]
    },
    statusFilter2(status) {
      const statusMap = {
        '1': '在线',
        '0': '下线'
      }
      return statusMap[status]
    }

  },
  data() {
    return {
      accountFormOptions: [{
        value: 1,
        label: '正常'
      }, {
        value: 0,
        label: '禁止'
      }],
      accountForm: {
        id: null,
        fromDate: null,
        toDate: null,
        cycle: null,
        accountNo: null,
        status: null,
        bandwidth: null,
        fromDate: null,
        fromDate: null,
        status: '1',
        addDay: 0
      },
      connections:0,// connections 是全局的不是row基本，坑
      accountDialog: false,

      roles: store.getters.roles,
      toColip: '',
      opAccountId: null,
      serverTotal: 0,
      chooseServerId: null,
      serverListQuery: {
        page: 1,
        pageSize: 10
      },
      dialogWidth: 0,
      serverDialog: false,
      serverList: null,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        pageSize: 50,
        userEmail: ''
      }
    }
  },
  computed: {

  },
  mounted() {
    window.onresize = () => {
      return (() => {
        this.setDialogWidth()
      })()
    }
  },
  created() {
    this.getList()
    this.setDialogWidth()
  },
  methods: {
    getConnection(row,id){
      getConnection(id).then(response =>{
        this.connections=response.obj;
          this.$message.success('成功')
      })
    },
    setDialogWidth() {
      console.log(document.body.clientWidth)
      var val = document.body.clientWidth
      const def = 800 // 默认宽度
      if (val < def) {
        this.dialogWidth = '100%'
      } else {
        this.dialogWidth = def + 'px'
      }
    },
    addToDate() {
      var toTime = this.accountForm.toDate
      this.accountForm.toDate = toTime + oneDayms * this.accountForm.addDay
    },
    handlerAccountCloseDialog(done) {
      done()
    },
    openAccountDidlog(row) {
      this.accountForm = row
      this.accountDialog = true
    //  console.log(row)
    //  this.accountForm.rangeDate= [new Date().setTime(this.accountForm.fromDate),new Date().setTime(this.accountForm.toDate)]
    },
    handlerCopy(text, event) {
      //   console.log(Base64.encode('dankogai'))
      clip(text, event)
    },
    submitUpdateAccount() {
      console.log(this.accountForm)
      this.accountForm.content = null
      updateAccount(this.accountForm).then(_ => {
        this.$message.success('提交成功')
        this.getList()
      })
    },
    submitUpdateServer() {
      if (!this.chooseServerId) {
        this.$message.error('请选择服务器')
        this.$refs.multipleTable.clearSelection()
        return
      }
      var data = { 'id': this.opAccountId, 'serverId': this.chooseServerId }
      updateAccountServer(data).then(response => {
        this.$message.success('提交成功,原账号将失效,请使用新账号')
        this.getList()
      })
    },
    handlerServerCloseDialog(done) {
      this.$refs.multipleTable.clearSelection()
      this.chooseServerId = null
      this.opAccountId = null
      done()
    },
    handleCurrentChange(rows) {
      this.chooseServerId = null
      if (rows.length > 1) {
        this.$message.error('只能选择一个服务器')

        return
      }
      if (rows.length < 1) {
        return
      }
      var row = rows[0]
      this.chooseServerId = row && row.id ? row.id : null
      console.log(this.chooseServerId)
    },
    getServerList() {
      serverList(this.serverListQuery).then(response => {
        this.serverList = response.obj.content
        this.serverTotal = response.obj.total
      })
    },

    changeServerDidlog(accountId) {
      this.serverDialog = true
      this.opAccountId = accountId
      this.getServerList()
    },
    formatDate(date) {
      if (!date) return ''
      var year = date.getFullYear()
      var month = date.getMonth() + 1
      var date = date.getDate()
      var hour = date.getHours()
      var minute = date.getMinutes()
      var second = date.getSeconds()
      return year + '-' + month + '-' + date + ' ' + hour + ':' + minute + ':' + second
    },
    getList() {
    // var isAdmin=this.roles.indexOf('admin')>-1;
      this.listLoading = true

      getAccounts(1).then(response => {
        this.list = response.obj.content
        for (var i = 0; i < this.list.length; i++) {
          var content = this.list[i].content
          this.list[i].content = content ? JSON.parse(content) : {}
          this.list[i].toColip = 'vmess://' + Base64.encode(content)
        }
        // console.log(this.list);
        this.total = response.obj.total
        this.listLoading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>
  .form-item{
    margin-bottom: 10px;
    }
  .card-panel-col {
    margin-right: 10px;
    margin-bottom: 20px;
  }
.dashboard-editor-container {

  padding: 10px;
 // background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
