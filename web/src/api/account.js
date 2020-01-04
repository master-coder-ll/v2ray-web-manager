import request from '@/utils/request'

/**
 * 获取一个用户的账号列表
 * @param {*} id
 */
export function getAccounts(id) {
  return request({
    url: `/account/${id}`,
    method: 'get'
  })
}

/**
 * 获取账号连接数
 * @param {*} id 
 */
export function getConnection(id) {
  return request({
    url: `account/connection/${id}`,
    method: 'get'
  })
}
/**
 * 获取所有用户的用户列表
 * @param {*} data
 */
export function accountsList(data) {
  return request({
    url: `/account`,
    method: 'get',
    params: data
  })
}
/**
 * 更新v2ray账号的服务器
 * @param {id,serverId} data
 */
export function updateAccountServer(data) {
  return request({
    url: `/account/server`,
    method: 'put',
    data
  })
}

/**
 * 更新v2ray账号
 * @param  data
 */
export function updateAccount(data) {
  return request({
    url: `/account`,
    method: 'put',
    data
  })
}

