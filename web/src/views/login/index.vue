<template>
  <div class="login-container">
     <github-corner class="github-corner" />
    <div hidden>{{ autoLogin }}</div>
    <div v-if="loginVisible" id="login">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

        <div class="title-container">
          <h3 class="title">登录</h3>
        </div>

        <el-form-item prop="email">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="username"
            v-model="loginForm.email"
            placeholder="email"
            name="email"
            type="text"
            tabindex="1"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="Password"
            name="password"
            tabindex="2"
            auto-complete="on"
            @keyup.enter.native="handleLogin"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>

        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">Login</el-button>

        <div class="tips">
          <el-row>
            <el-col :span="9" :push="1">
              <el-button type="text" @click.native.prevent="onVisible('reg')"> 注册</el-button>

            </el-col>
            <el-col :span="8" :push="11"> <el-button type="text" @click.native.prevent="onVisible('forgot')">忘记密码?</el-button></el-col>

          </el-row>
        </div>

      </el-form>
    </div>
    <div v-if="regVisible" id="reg">

      <el-form ref="regForm" :model="regForm" :rules="regRules" class="login-form" auto-complete="on" label-position="left">
        <div class="title-container">
          <h3 class="title">注册 </h3>
        </div>

        <el-form-item prop="email">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="email"
            v-model="regForm.email"
            placeholder="email"
            name="email"
            type="text"
            tabindex="21"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="vCode">
          <el-col :span="12"> <el-input
            ref="vCode"
            v-model="regForm.vCode"
            placeholder="验证码"
            name="vCode"
            type="text"
            width="50%"
            tabindex="22"
          /></el-col>
          <el-col :span="12" :push="5">  <el-button type="text" :disabled="wait_timer>0" @click.native.prevent="getVcode('regForm','reg')">{{ VcodeContext }}</el-button></el-col>

        </el-form-item>
        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="regForm.password"
            :type="passwordType"
            placeholder="Password"
            name="password"
            tabindex="23"
          />
        </el-form-item>
        <el-form-item prop="password2">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password2"
            v-model="regForm.password2"
            :type="passwordType"
            placeholder="再次输入你的密码"
            name="password2"
            tabindex="24"
          />
        </el-form-item>

        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="reg">提交</el-button>
        <div class="tips">
          <el-row>
            <el-col :span="24">

              <el-button type="text" @click.native.prevent="offVisible">返回</el-button>
            </el-col>

          </el-row>
        </div>

      </el-form>
    </div>

    <div v-if="fgVisible" id="forgot">

      <el-form ref="forgotForm" :model="forgotForm" :rules="forgotRules" class="login-form" auto-complete="on" label-position="left">
        <div class="title-container">
          <h3 class="title">找回密码 </h3>
        </div>

        <el-form-item prop="email">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="email"
            v-model="forgotForm.email"
            placeholder="email"
            name="email"
            type="text"
            tabindex="21"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="vCode">
          <el-col :span="12"> <el-input
            ref="vCode"
            v-model="forgotForm.vCode"
            placeholder="验证码"
            name="vCode"
            type="text"
            width="50%"
            tabindex="22"
          /></el-col>
          <el-col :span="12" :push="5">  <el-button type="text" :disabled="wait_timer>0" @click.native.prevent="getVcode('forgotForm','forgot')">{{ VcodeContext }}</el-button></el-col>

        </el-form-item>
        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="forgotForm.password"
            :type="passwordType"
            placeholder="Password"
            name="password"
            tabindex="23"
          />
        </el-form-item>
        <el-form-item prop="password2">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password2"
            v-model="forgotForm.password2"
            :type="passwordType"
            placeholder="再次输入你的密码"
            name="password2"
            tabindex="24"
          />
        </el-form-item>

        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="forgot">修改密码</el-button>
        <div class="tips">
          <el-row>

            <el-col :span="24"> <el-button type="text" @click.native.prevent="offVisible">返回</el-button></el-col>

          </el-row>
        </div>

      </el-form>
    </div>
  </div>

</template>

<script>
import { validEmail } from '@/utils/validate'
import { sendEmail } from '@/api/email'
import { reg, forgot } from '@/api/user'
import md5  from 'js-md5'
import GithubCorner from '@/components/GithubCorner'



export default {
  name: 'Login',
  components :{GithubCorner},
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validEmail(value)) {
        callback(new Error('请输入正确的email'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码大于6位'))
      } else {
        callback()
      }
    }
    const validatePassword2 = (rule, value, callback) => {
      if (this.regForm.password !== value) {
        callback(new Error('密码不相同'))
        return
      }
      callback()
    }
    const validatefogotPassword2 = (rule, value, callback) => {
      if (this.forgotForm.password !== value) {
        callback(new Error('密码不相同'))
        return
      }
      callback()
    }
    return {
      forgotForm: {
        email: '',
        password: '',
        password2: '',
        vCode: ''

      },
      regForm: {
        email: '',
        password: '',
        password2: '',
        vCode: ''

      },
      forgotRules: {
        email: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
        vCode: [{ required: true, trigger: 'blur', message: '请输入正确的验证码' }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        password2: [{ required: true, trigger: 'blur', validator: validatefogotPassword2 }]
      },
      regRules: {
        email: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
        vCode: [{ required: true, trigger: 'blur', message: '请输入正确的验证码' }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        password2: [{ required: true, trigger: 'blur', validator: validatePassword2 }]
      },
      loginForm: {
        email: '',
        password: ''
      },
      loginRules: {
        email: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      fgVisible: false,
      regVisible: false,
      loginVisible: true,
      VcodeContext: '获取验证码',
      wait_timer: 0
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    getVcode(form, type) {
      var email = this.$refs[form].model.email

      if (!validEmail(email)) {
        this.$message.error('email不正确,不能获取验证码')
      } else {
        sendEmail(email, type).then(resp => {
          this.$message.success('邮件可能出现在你的垃圾箱中,请注意。')
          this.wait_timer = 89
          var that = this
          var vcodeContext = this.VcodeContext
          var timer_interval = setInterval(function() {
            if (that.wait_timer > 0) {
              that.wait_timer--
              that.VcodeContext = that.wait_timer
            } else {
              that.VcodeContext = vcodeContext
              clearInterval(timer_interval)
            }
          }, 1000)
        })
      }
    },
    forgot() {
      this.$refs.forgotForm.validate(valid => {
        if (valid) {
          this.loading = true

            const formData= Object.assign({},this.forgotForm )
           formData.password=md5(formData.password)
          forgot(formData).then(resp => {
            this.$message({
              message: '修改成功',
              type: 'success'
            })
            this.offVisible()
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          this.loading = false
          return false
        }
      })
    },
    reg() {
      this.$refs.regForm.validate(valid => {
        if (valid) {
          this.loading = true
          const formData= Object.assign({},this.regForm )
           formData.password=md5(formData.password)
          reg(formData).then(resp => {
            this.$message({
              message: '注册成功',
              type: 'success'
            })
            this.offVisible()
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          this.loading = false
          return false
        }
      })
    },
    onVisible(type) {
      if (type === 'reg') {
        this.fgVisible = false
        this.loginVisible = false
        this.regVisible = true
      }

      if (type === 'forgot') {
        this.fgVisible = true
        this.loginVisible = false
        this.regVisible = false
      }
    },
    offVisible() {
      this.loginVisible = true
      this.fgVisible = false
      this.regVisible = false
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    autoLogin() {
      this.$store.dispatch('user/info').then(() => {
        this.$router.push({ path: this.redirect || '/' })
      }
      )
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
            const formData= Object.assign({},this.loginForm )
           formData.password=md5(formData.password)
          this.$store.dispatch('user/login', formData).then(() => {
            console.log('2')
            this.$router.push({ path: this.redirect || '/' })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
   // color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
     caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
   color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }
</style>
