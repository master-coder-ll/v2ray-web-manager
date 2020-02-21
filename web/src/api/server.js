import request from '@/utils/request'

export function addServer(data) {
  return request({
    url: '/server',
    method: 'post',
    data
  })
}
/**
 * 更新
 * @param {} data
 */
export function updateServer(data) {
  return request({
    url: '/server',
    method: 'put',
    data
  })
}

/**
 * 获取一个server
 * @param {} id
 */
export function getServer(id) {
  return request({
    url: `/server/${id}`,
    method: 'get'
  })
}
/**
 * list admin
 * @param {int}} page
 * @param {int} pageSize
 */
export function serverList(page) {
  return request({
    url: `/server`,
    method: 'get',
    params: page
  })
}
/**
 * 获取可用的服务器列表 vip

 */
export function availableServers() {
  return request({
    url: `/server/findServersForAccount`,
    method: 'get',
  })
}
/**
 * 删除
 * @param {*} id
 */
export function deleteServer(id) {
  return request({
    url: `/server/${id}`,
    method: 'DELETE'
  })
}
