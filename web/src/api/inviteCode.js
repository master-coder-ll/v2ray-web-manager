import request from '@/utils/request'

/**
 * 获取邀请码列表
 * @param {*} page
 */
export function list(page) {
  return request({
    url: `invite-code`,
    method: 'get',
    params: page
  })
}

/**
 * 删除
 * @param {*} id
 */
export function del(id) {
  return request({
    url: `invite-code/${id}`,
    method: 'DELETE'
  })
}
/**
 * 更新v2ray账号
 * @param  data
 */
export function generate(count) {
  return request({
    url: `invite-code`,
    method: 'post'
  })
}

