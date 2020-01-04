import request from '@/utils/request'

export function sendEmail(email, type) {
  return request({
    url: '/user/send-email',
    method: 'get',
    params: { 'email': email, 'type': type }
  })
}
