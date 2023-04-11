import requests
import json

URL_PREFIX = 'http://localhost:8080'
url_mapping = {
	'login': ('/api/v1/members/login', 'POST', ['id', 'password']),
	'list member': ('/api/v1/members', 'GET', []),
	'signup': ('/api/v1/members', 'POST', ['id', 'password', '-name']),
	
	
	'list chat by room': ('/api/v1/chat/personal_chat/{oppositeUserId}', 'GET', ['oppositeUserId', '-prevId', '-size']),
	'list room': ('/api/v1/chat/room', 'GET', []),
	'enter room': ('/api/v1/chat/personal_chat/{oppositeUserId}/enter', 'GET', ['oppositeUserId', '-size']),
	'send chat': ('/api/v1/chat', 'POST', ['receiverUserId', 'content']),
	'delete chat': ('/api/v1/chat/{chatId}', 'DELETE', ['chatId']),
	'list all received chat': ('/api/v1/chat/received', 'GET', ['-prevId', '-size'])
	'xxx': ('x', 'x', [])
}

global_cookie = dict()
global_header = dict()

command_list = [
	(None, None),  # not used index 0
	('회원 가입', 'signup'),
	('로그인', 'login'),
	('방 목록', 'list room'),
	('방 입장', 'enter room'),
	('방 만들고 채팅 보내기', None),  # make new room and send chat
	('채팅 메시지 보내기', 'send chat'),
	('채팅 메시지 삭제', 'delete chat'),
	('수신한 모든 채팅 메시지 목록', 'list all received chat')
]


def request_api(url, method = 'GET', payload = None):
	full_url = URL_PREFIX + url
	print(f'url={full_url}, method={method}, payload={payload}')
	if method.upper() == 'GET':
		r = requests.request(method, full_url, params=payload, cookies=global_cookie, headers=global_header)
	else:
		r = requests.request(method, full_url, data=payload, cookies=global_cookie, headers=global_header)
	if not 200 <= r.status_code < 300:
		print(f'[{r.status_code}] message={r.text}')
		return {"message": r.text}, dict(r.headers)
	return json.loads(r.text), dict(r.headers)


def injection_pathvariable(url, param = None):
	if param is None:
		return url
	return url.format(**param)


if __name__ == "__main__":
	print('Press \'q\' to exit.')
	while True:
		line = input('> ').rstrip().lower()
		if line == 'q':
			break
		if line not in url_mapping:
			continue
		
		url, method, param_li = url_mapping[line]
		payload = dict()
		for param in param_li:
			if param[0] != '-':
				while True:
					val = input(f'파라미터 값 입력 (필수): {param}=').rstrip()
					if val != "":
						break
				payload[param] = val
			else:
				param = param[1:]
				val = input(f'파라미터 값 입력 (생략 가능): {param}=').rstrip()
				if val != "":
					payload[param] = val
		
		url = injection_pathvariable(url, payload)
		body_json, header = request_api(url, method, payload)
		# 헤더 출력
		#print(json.dumps(header, indent=2))
		# 바디 출력
		print(json.dumps(body_json, indent=2))
		if 'Set-Cookie' in header:
			temp = header['Set-Cookie']
			temp = temp.split(';')[0]
			k, v = temp.split('=')
			print(k, v)
			global_cookie[k] = v
		if 'Authorization' in header:
			token = header['Authorization']
			print(token)
			global_header['Authorization'] = token
	
	