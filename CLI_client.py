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
	'get chat by id': ('/api/v1/chat/{chatId}', 'GET', ['chatId']),
	'send chat': ('/api/v1/chat', 'POST', ['receiverUserId', 'content']),
	'delete chat': ('/api/v1/chat/{chatId}', 'DELETE', ['chatId']),
	'list all received chat': ('/api/v1/chat/received', 'GET', ['-prevId', '-size']),
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
		return None, dict(r.headers)
	return json.loads(r.text), dict(r.headers)


def injection_pathvariable(url, param = None):
	if param is None:
		return url
	return url.format(**param)


def get_chat_by_id(chat_id):
	full_url = URL_PREFIX + injection_pathvariable(url_mapping['get chat by id'][0], {'chatId': chat_id})
	r = requests.request('GET', full_url, cookies=global_cookie, headers=global_header)
	if not 200 <= r.status_code < 300:
		print(f'[{r.status_code}] message={r.text}')
		return None
	return json.loads(r.text).get('data')


def prompt_command_list():
	print('-' * 30)
	for i, command in enumerate(command_list):
		if i == 0: continue
		print(f'{i}) {command[0]}')
	print('-' * 30)
	return

def run_command(command, show = True):
	url, method, param_li = url_mapping[command]
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
	# if show:
	#	print(json.dumps(header, indent=2))
	if body_json is None:
		return
	
	if show:
		# 바디 출력
		print(json.dumps(body_json, indent=2))
		# 깔끔한 출력
		pretty_print(body_json, command)
	
	if 'Set-Cookie' in header:
		temp = header['Set-Cookie']
		temp = temp.split(';')[0]
		k, v = temp.split('=')
		#print(k, v)
		global_cookie[k] = v
	if 'Authorization' in header:
		token = header['Authorization']
		print(token)
		global_header['Authorization'] = token
	return

def pretty_print(js, commmand):
	print('=' * 30)
	if command == 'list room':
		for t in js['data']:
			opposite_user_id = t['first']
			chat_id = t['second']
			chat = get_chat_by_id(chat_id)
			print(f"[1:1 채팅방] {opposite_user_id}와의 대화 / {chat['senderUserId']} : {chat['content']} (보낸 시간 : {chat['created_at']}, 읽은 시간 : {chat['read_at']})")
	elif command in ['enter room', 'list all received chat']:
		for chat in js['data']['list']:
			print(f"[id={chat['id']}] {chat['senderUserId']} : {chat['content']} (보낸 시간 : {chat['created_at']}, 읽은 시간 : {chat['read_at']})")
	elif command == 'list member':
		for member in js['data']:
			print(f"id={member['id']}, name={member['name']}, status message={member['statusMessage']}")
		
	return

if __name__ == "__main__":
	print('커맨드를 번호로 입력')
	print('종료할 때는 q 입력')
	while True:
		prompt_command_list()
		line = input('> ').rstrip().lower()
		if line == 'q':
			break
		try:
			command_idx = int(line)
		except:
			continue
		
		if not 1 <= command_idx <= len(command_list):
			continue
		
		if command_list[command_idx][0] == '방 만들고 채팅 보내기':
			run_command('list member', False)
			run_command('send chat')
		else:
			_, command = command_list[command_idx]
			run_command(command)
			
		
	
	