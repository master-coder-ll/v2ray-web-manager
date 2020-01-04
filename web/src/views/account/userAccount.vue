<template>
  <div class="app-container ">

    <el-table v-loading="listLoading" :data="list" fit highlight-current-row style="width: 100%">

      <el-table-column width="400px" align="left" label="基本信息">
        <template slot-scope="scope">
          <div><span>用户：{{ scope.row.email }}</span></div>
          <div><span>账号：{{ scope.row.accountNo }}</span></div>

          <div>
            <span> 有效时间： {{ scope.row.fromDate | parseTime('{y}-{m}-{d} {h}:{i}') }} - </span>
            <span>
              <font v-if="scope.row.toDate>new Date().getTime()">  {{ scope.row.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
              <font v-else color="red">  {{ scope.row.toDate | parseTime('{y}-{m}-{d} {h}:{i}') }}</font>
            </span>
          </div>
          <div v-if="scope.row.stat">

            <span>结算时间：{{scope.row.stat.toDate  | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
          </div>

          <div> <span>速率：{{ scope.row.speed | speedFilter }}</span></div>
          <div>周期：{{ scope.row.cycle }}天/周期</div>
          <div>流量：<span>
            <font v-if="(scope.row.stat?(scope.row.stat.flow/1024/1024/1024).toFixed(2) : 0)<scope.row.bandwidth">{{ scope.row.stat?(scope.row.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
            <font v-else color="red">{{ scope.row.stat?(scope.row.stat.flow/1024/1024/1024).toFixed(2) : 0 }}</font>
            /{{ scope.row.bandwidth }}GB/周期</span>
          </div>
          <div>连接数：{{ scope.row.maxConnection }}/账号</div>
          <el-tag v-permission="['admin']">
            <div> <el-link icon="el-icon-edit" type="primary" @click="openAccountDidlog(scope.row)">编辑账号</el-link> </div>
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column width="400px" align="left" label="服务器信息">
        <template slot-scope="scope">
          <div v-if="scope.row.server">

            <div>服务器名称：{{ scope.row.server.serverName }}</div>
            <div>服务器地址：{{ scope.row.server.clientDomain }}</div>
            <div>流量倍数：{{ scope.row.server.multiple }}</div>
            <div>服务器状态：{{ scope.row.server.status |statusFilter2 }}</div>
            <div> <el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(scope.row.id)">更改服务器</el-link> </div>
          </div>
          <div v-else><el-link icon="el-icon-edit" type="primary" @click="changeServerDidlog(scope.row.id)">选择你的服务器</el-link> </div>

        </template>
      </el-table-column>

      <el-table-column width="400px" align="left" label="v2ray账号信息">
        <template slot-scope="scope">
          <div v-if="scope.row.content.add">
            <div>地址:{{ scope.row.content.add }}</div>
            <div>端口:{{ scope.row.content.port }}</div>
            <div>用户Id:{{ scope.row.content.id }}</div>
            <div>额外Id:{{ scope.row.content.aid }}</div>
            <div>加密方式:auto</div>
            <div>传输协议:{{ scope.row.content.net }}</div>
            <div>伪装类型:{{ scope.row.content.type }}</div>
            <div>传输域名(host):{{ scope.row.content.host }}</div>
            <div>路径(path):{{ scope.row.content.path }}</div>
            <div>底层传输安全(tls):{{ scope.row.content.tls }}</div>
          </div>
          <div v-else />
        </template>
      </el-table-column>

      <el-table-column width="400px" align="left" label="操作">
        <template slot-scope="scope">
          <div v-if="scope.row.content.add">
            <el-row>
              <el-col :span="12"><el-input v-model="scope.row.toColip" /></el-col>
              <el-col :span="12" class="margin-left:10px"><el-button @click="handlerCopy(scope.row.toColip,$event)">复制</el-button></el-col>
            </el-row>
            <vue-qr :text="scope.row.toColip" qid="qrcode" />

          </div>
          <div v-else />
        </template>
      </el-table-column>

    </el-table>
    <div class="margin-left:10px; margin-top:20px; margin-bottom:10px">
      <el-tag v-permission="['admin']">

        <el-row>
          <el-input v-model="listQuery.userEmail" />  <el-button @click="getList">搜索</el-button>
        </el-row>
      </el-tag>
    </div>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <!-- 账号管理 -->
    <el-dialog title="服务器列表" :visible.sync="accountDialog" :before-close="handlerAccountCloseDialog">
      <el-form ref="accountForm" :model="accountForm" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="accountForm.accountNo" />
        </el-form-item>
        <el-form-item label="周期">
          <el-input v-model="accountForm.cycle" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-col :span="12">
            <el-date-picker
              v-model="accountForm.fromDate"
              value-format="timestamp"
              type="datetime"
            />
          </el-col>
          <el-col :span="11" :push="1">
            <el-date-picker
              v-model="accountForm.toDate"
              value-format="timestamp"
              type="datetime"
            />
          </el-col>
          <!-- <el-input v-model="accountForm.fromDate"></el-input> -->
        </el-form-item>
        <el-form-item label="to">

          <el-col :span="16"> <el-input v-model="accountForm.addDay" /></el-col>
          <el-col :span="7" :push="1">    <el-link type="primary" @click="addToDate">增加N天</el-link></el-col>

        </el-form-item>
        <el-form-item label="速率">
          <el-input v-model="accountForm.speed" />
        </el-form-item>
        <el-form-item label="流量">
          <el-input v-model="accountForm.bandwidth" />
        </el-form-item>
        <el-form-item label="连接数">
          <el-input v-model="accountForm.maxConnection" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="accountForm.status" placeholder="状态">
            <el-option
              v-for="item in accountFormOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>

          <el-button @click="submitUpdateAccount">提交</el-button>

        </el-form-item>
      </el-form>

    </el-dialog>
    <!-- 服务器列表 -->
    <el-dialog title="服务器列表" :visible.sync="serverDialog" :before-close="handlerServerCloseDialog">

      <el-table ref="multipleTable" :data="serverList" fit highlight-current-row style="width: 100%" @selection-change="handleCurrentChange">

        <el-table-column
          type="selection"
          width="55"
        />

        <el-table-column width="150px" align="center" label="服务器名称">
          <template slot-scope="scope">
            <span>{{ scope.row.serverName }}</span>
          </template>
        </el-table-column>

        <el-table-column width="150px" align="center" label="域名">
          <template slot-scope="scope">
            <span>{{ scope.row.clientDomain }}</span>
          </template>
        </el-table-column>

        <el-table-column width="200px" max-width="100%" align="center" label="描述">
          <template slot-scope="scope">
            <span>{{ scope.row.desc }}</span>
          </template>
        </el-table-column>

        <el-table-column width="80px" align="center" label="状态">
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
  </div>
</template>

<script>
import { getAccounts, accountsList, updateAccountServer, updateAccount } from '@/api/account'
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
  created() {
    this.getList()
  },
  methods: {
    onChange(v) {
      console.log(v)
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
      var isAdmin = this.roles.indexOf('admin') > -1
      this.listLoading = true
      var req = isAdmin ? accountsList(this.listQuery) : getAccounts(1)
      req.then(response => {
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
<style scoped>

.mainDiv {
    margin-left: 10%;
    margin-right: 10%;

}
.box-card {
    width: 480px;
  }
</style>
