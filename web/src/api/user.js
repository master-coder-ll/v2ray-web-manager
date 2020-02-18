import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function reg(data) {
  return request({
    url: '/user/reg',
    method: 'post',
    params: { 'vCode': data.vCode },
    data
  })
}

export function forgot(data) {
  return request({
    url: '/user/forgot',
    method: 'post',
    params: { 'vCode': data.vCode },
    data
  })
}
export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'get'
  })
}
/**
 * admin 获取user列表
 * @param {} pageable
 */
export function userList(pageable) {
  return request({
    url: '/user',
    method: 'get',
    params: pageable
  })
}

/**
 * admin 新增一个用户
 * @param {*} user
 */
export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

/**
 * 添加备注
 * addRemark
 * @param {int id ,string remark} data 
 */
export function addremark(data) {
  return request({
    url: 'user/addRemark',
    method: 'post',
    data
  })
}
/**
 * admin 获取一个用户信息
 * @param {}} id
 */
export function getUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}
/**
 * 删除用户
 * @param {}} id
 */
export function delUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 更新用户状态
 * @param {}} user
 */
export function updateUserStatus(data) {
  return request({
    url: '/user/status',
    method: 'put',
    data
  })
}
