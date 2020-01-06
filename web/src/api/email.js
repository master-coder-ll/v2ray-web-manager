import request from '@/utils/request'

export function sendEmail(email, type,inviteCode) {
  return request({
    url: '/user/send-email',
    method: 'get',
    params: { 'email': email, 'type': type ,'inviteCode':inviteCode}
  })
}
