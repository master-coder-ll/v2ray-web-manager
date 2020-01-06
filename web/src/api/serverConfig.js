import request from '@/utils/request'

export function addServerConfig(data) {
  return request({
    url: 'serverConfig',
    method: 'post',
    data
  })
}
/**
 * 更新
 * @param {} data
 */
export function updateServerConfig(data) {
  return request({
    url: 'serverConfig',
    method: 'put',
    data
  })
}

/**
 * 获取一个server
 * @param {} id
 */
export function getServerConfig(id) {
  return request({
    url: `serverConfig/${id}`,
    method: 'get'
  })
}
/**
 * list
 * @param {int}} page
 * @param {int} pageSize
 */
export function listServerConfig(page) {
  return request({
    url: `serverConfig`,
    method: 'get',
    params: page
  })
}

/**
 * 删除
 * @param {*} id
 */
export function deleteServerConfig(id) {
  return request({
    url: `serverConfig/${id}`,
    method: 'DELETE'
  })
}
