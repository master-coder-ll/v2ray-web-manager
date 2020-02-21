<template>
  <div class="dashboard-editor-container">
    <el-form size="mini" v-if="account">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
        <div slot="header" >
          
       账号: {{ account.accountNo }}  
         <!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
       </div>
          

            <el-form-item label="等级:" >
            {{ account.level |levelFilter }}
            </el-form-item>
          <el-form-item label=" 有效时间:" >
              <!--  {{ account.fromDate | parseTime('{y}-{m}-{d} {h}:{i}') }} - -->
        
            <span>
              <font v-if="account.toDate>new Date().getTime()">  {{ account.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
              <font v-else color="red">  {{ account.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
            </span>
          </el-form-item>

            <el-form-item label=" 结算时间:" v-if="account.stat">
              <span>{{account.stat.toDate  | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
            </el-form-item>
          <el-form-item label="速率:" >
            <span></span>
            <span>{{ account.speed | speedFilter }}</span>
            </el-form-item>
          <el-form-item label="周期:" >
            <!-- <span>周期：</span> -->
            {{ account.cycle }}天/周期
            </el-form-item>
          <el-form-item label="流量:">
            
       
            <span>
              <font v-if="(account.stat?(account.stat.flow/1024/1024/1024).toFixed(2) : 0)<account.bandwidth">{{ account.stat?(account.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
              <font v-else color="red">{{ account.stat?(account.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
              /{{ account.bandwidth }}GB/周期</span>
          </el-form-item>
          <el-form-item label="连接数:">{{ account.maxConnection }}/账号
             </el-form-item>
        
           
            <el-form-item label="订阅地址(推荐):">
                 <el-col :xs="24" :sm="6" :lg="6" >
               <el-input v-model="account.subscriptionUrl" >  <el-button slot="prepend" @click="generatorNewSubscriptionUrl()">
                 <div v-if="!account.subscriptionUrl">生成</div><div v-if="account.subscriptionUrl">更新</div>
               </el-button> <el-button slot="append" @click="handlerCopy(account.subscriptionUrl,$event)">复制</el-button> </el-input>
           
             </el-col >
             
               </el-form-item>
            
          
   
        </el-card>
      </el-col>

      <!-- <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
          <div slot="header" >
          服务器信息
          </div>
          <div v-if="account.server">
           <el-form-item label="服务器名称:">   {{ account.server.serverName  }}  </el-form-item>
            <el-form-item label="服务器地址:">{{ account.server.clientDomain }}</el-form-item>
            <el-form-item label="流量倍数:">{{ account.server.multiple }}</el-form-item>
            <el-form-item label="服务器状态:">{{ account.server.status |statusFilter2 }}</el-form-item>
            <div> <el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(account.id)">更改服务器</el-link> </div>
          </div>
          <div v-else><el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(account.id)">选择你的服务器</el-link> </div>
        </el-card>
      </el-col> -->

      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-card>
            <div slot="header" >
            v2ray账号
          </div>

        
            <el-row >
             
             <el-col :xs="24" :sm="24" :lg="12">
               
                  <el-form-item label="服务器:">
                    <el-select v-model="serverId" @change="serverChange" placeholder="请选择服务器">
                         <el-option
                        v-for="item in serverList"
                        :key="item.value"
                          :label="item.label"
                         :value="item.value">
                       </el-option>
                  
                      </el-select>
                    </el-form-item>  
             <div v-if="v2rayAccount">       
            <el-form-item label="地址:">{{ v2rayAccount.add }}</el-form-item>
            <el-form-item label="端口:">{{ v2rayAccount.port }}</el-form-item>
            <el-form-item label="用户Id:">{{ v2rayAccount.id }}</el-form-item>
            <el-form-item label="额外Id(alterId):">{{ v2rayAccount.aid }}</el-form-item>
            <el-form-item label="加密方式:">auto</el-form-item>
            <el-form-item label="传输协议:">{{ v2rayAccount.net }}</el-form-item>
            <el-form-item label="伪装类型:">{{ v2rayAccount.type }}</el-form-item>
            <el-form-item label="传输域名(host):">{{ v2rayAccount.host }}</el-form-item>
            <el-form-item label="路径(path):">{{ v2rayAccount.path }}</el-form-item>
            <el-form-item label="底层传输安全(tls):">{{ v2rayAccount.tls }}</el-form-item>
            <el-form-item label="服务器描述:">{{ server.desc }}</el-form-item>
             </div>
               </el-col> 
            
              <el-col :xs="24" :sm="24" :lg="12" >
                   <div v-if="v2rayAccount">
              <el-form-item label="" >
              <el-col :xs="24" :sm="24" :lg="6">
                   <el-input v-model="toColip"> <el-button slot="append" @click="handlerCopy(toColip,$event)">复制</el-button> </el-input>
              </el-col>
            </el-form-item>
            <el-form-item label="">
              <vue-qr :text="toColip" qid="qrcode" />
            </el-form-item>

            
                    </div>
              </el-col>
        
            </el-row>
           
         
         
        </el-card>
      </el-col>
  

    </el-row>
    
    </el-form>
  </div>
</template>
<script>
import { getAccount, accountsList,  updateAccount,getConnection,getV2rayAccount,generatorSubscriptionUrl} from '@/api/account'
import { availableServers,getServer} from '@/api/server'
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
    levelFilter(status) {
      const statusMap = {
        0: '等级0',
        1: '等级1',
        2: '等级2',
        3: '等级3'
      }
      return statusMap[status]
    }
    ,
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
      serverId:null,
      server:null,
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
      v2rayAccount:null,
      toColip: '',
      opAccountId: null,
      serverTotal: 0,
      chooseServerId: null,
      serverListQuery: {
        page: 1,
        pageSize: 10
      },
      isEdit:false,
      serverList: null,
      account: null,
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
  created() {
    this.getRemoteAccount()
    this.getServerList()
  },
  methods: {
   generatorNewSubscriptionUrl(){

   
     if(this.isEdit){
      this.$confirm('确认更新操作？成功原订阅地址将失效。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          roundButton:true,
          center:false
        }).then(() => {
            this.trueGeneratorSubscriptionUrl()
    
        }).catch(() => {
            
        });
     }else{
       this.trueGeneratorSubscriptionUrl()
     }
     
      
    
   },
   trueGeneratorSubscriptionUrl(){
       generatorSubscriptionUrl().then(response =>{
        this.getRemoteAccount();
        this.$message.success('更新订阅地址成功，原地址已经失效')
   })
   },
    handlerCopy(text, event) {
      //   console.log(Base64.encode('dankogai'))
      clip(text, event)
    }
   ,serverChange(){
     getServer(this.serverId).then(response =>{
       this.server=response.obj
     })
     getV2rayAccount({'serverId':this.serverId}).then(response =>{
        this.v2rayAccount=response.obj
        this.toColip = 'vmess://' + Base64.encode(JSON.stringify(this.v2rayAccount))
     })
   },
    getServerList() {
      availableServers().then(response => {
        this.serverList=[]
  
         
        for( var i in response.obj){
         var server= response.obj[i]
            var localserver={}
            localserver.value=server.id
            localserver.label=server.serverName
            this.serverList[i]=localserver
          
        }
        
      })
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
    getRemoteAccount() {
    // var isAdmin=this.roles.indexOf('admin')>-1;


      getAccount(1).then(response => {
      this.account = response.obj
        this.isEdit= this.account.subscriptionUrl?true:false
        // for (var i = 0; i < this.list.length; i++) {
        //   var content = this.list[i].content
        //   this.list[i].content = content ? JSON.parse(content) : {}
        //   this.list[i].toColip = 'vmess://' + Base64.encode(content)
        // }
        // console.log(this.list);

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
