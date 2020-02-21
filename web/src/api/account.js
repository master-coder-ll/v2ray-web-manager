import request from '@/utils/request'

/**
 * 获取一个用户的账号列表
 * @param {*} id
 */
export function getAccount(id) {
  return request({
    url: `/account/${id}`,
    method: 'get'
  })
}


/**
 * 生成新的订阅地址
 * @param {type} data type 0,通用 1以上预留
 */
export function generatorSubscriptionUrl(data) {
  return request({
    url: `/account/generatorSubscriptionUrl`,
    method: 'get',
    data
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
 * 根据服务器获取V2rayAccount
 * @param {serverId} data serverId
 */
export function getV2rayAccount(data) {
  return request({
    url: `/account/v2rayAccount`,
    method: 'get',
    params: data
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

