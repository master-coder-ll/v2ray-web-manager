import request from '@/utils/request'

export function listNotice() {
  return request({
    url: 'notice',
    method: 'get',  
  })
}

export function addNotice(data) {
  return request({
    url: 'notice',
    method: 'post',
    data
  })
}

export function updateNotice(data) {
  return request({
    url: 'notice',
    method: 'put',
    data
  })
}
export function getNotice(id) {
  return request({
    url: `notice/${id}`,
    method: 'get'
  })
}
export function delNotice(id) {
  return request({
    url: `notice/${id}`,
    method: 'delete'
  })
}