import requests
import json

URL_PREFIX = 'http://localhost:8080'
URL_MAPPING = {
	'signup': ('/api/v1/members', 'POST', ['id', 'password', '-name']),
	'login': ('/api/v1/members/login', 'POST', ['id', 'password']),
	'list member': ('/api/v1/members', 'GET', []),
	'update member info': ('/api/v1/members', 'PUT', ['-name', '-statusMessage', '-password']),
	
	
	'list chat by room': ('/api/v1/chat/personal_chat/{oppositeUserId}', 'GET', ['oppositeUserId', '-nextId', '-size']),
	'list room': ('/api/v1/chat/room', 'GET', []),
	'enter room': ('/api/v1/chat/personal_chat/{oppositeUserId}/enter', 'GET', ['oppositeUserId', '-size']),
	'get chat by id': ('/api/v1/chat/{chatId}', 'GET', ['chatId']),
	'send chat': ('/api/v1/chat', 'POST', ['receiverUserId', 'content']),
	'delete chat': ('/api/v1/chat/{chatId}', 'DELETE', ['chatId']),
	'list all received chat': ('/api/v1/chat/received', 'GET', ['-nextId', '-size'])
}

global_cookie = dict()
global_header = dict()
# None, 'room list', 'in room'
status = None
global_opposite_user_id = None
global_next_chat_id = None

COMMAND_LIST = {
	'signup': 		('회원 가입', 'signup'),
	'login': 		('로그인', 'login'),
	'list member': 	('전체 유저 목록', 'list member'),
	'update member': 	('유저 정보 변경', 'update member info'),
	'list room': 	('방 목록', 'list room'),
	'enter room': 	('방 입장', 'enter room'),
	'new room': 	('방 만들고 입장', None),
	'list chat': 	('현재 방의 메시지 보기', 'list chat by room'),
	'list more chat': ('메시지 더보기', 'list chat by room'),
	'send chat': 	('채팅 메시지 보내기', 'send chat'),
	'delete chat': 	('채팅 메시지 삭제', 'delete chat'),
	'logout': 		('로그아웃', None),
	'exit room': 	('방 나가기', 'list room'),
	'list all received chat': ('수신한 모든 채팅 메시지 목록', 'list all received chat'),
	'quit':			('종료', None)
}


def request_api(url, method = 'GET', payload = None):
	full_url = URL_PREFIX + url
	print(f'url={full_url}, method={method}, payload={payload}')
	if method.upper() == 'GET':
		r = requests.request(method, full_url, params=payload, cookies=global_cookie, headers=global_header)
	else:
		r = requests.request(method, full_url, json=payload, cookies=global_cookie, headers=global_header)
	if not 200 <= r.status_code < 300:
		print(f'[{r.status_code}] message={r.text}')
		return None, dict(r.headers)
	return json.loads(r.text), dict(r.headers)


def injection_pathvariable(url, param = None):
	if param is None:
		return url
	return url.format(**param)


def get_chat_by_id(chat_id):
	full_url = URL_PREFIX + injection_pathvariable(URL_MAPPING['get chat by id'][0], {'chatId': chat_id})
	r = requests.request('GET', full_url, cookies=global_cookie, headers=global_header)
	if not 200 <= r.status_code < 300:
		print(f'[{r.status_code}] message={r.text}')
		return None
	return json.loads(r.text)


def prompt_command_list(candidate):
	print('-' * 60)
	for i, t in enumerate(candidate):
		print(f'{i+1}) {COMMAND_LIST[t][0]}')
	print('-' * 60)
	while True:
		line = input('> ').rstrip().lower()
		if line == 'q':
			exit(0)
		try:
			command_idx = int(line)
		except:
			continue
		
		if not 1 <= command_idx <= len(candidate):
			continue
		break
	return candidate[command_idx-1]


def run_command(command, payload = None, show = True):
	global global_opposite_user_id
	url, method, param_li = URL_MAPPING[command]
	if payload is None:
		payload = dict()
	for param in param_li:
		is_optional = param[0] == '-'
		if is_optional:
			param = param[1:]
		if param in payload:
			continue
		if not is_optional:
			while True:
				val = input(f'파라미터 값 입력 (필수): {param}=').rstrip()
				if val != "":
					break
			payload[param] = val
		else:
			val = input(f'파라미터 값 입력 (생략 가능): {param}=').rstrip()
			if val != "":
				payload[param] = val
	
	url = injection_pathvariable(url, payload)
	body_json, header = request_api(url, method, payload)
	# 헤더 출력
	#if show:
	#	print(json.dumps(header, indent=2))
	
	# 결과가 에러인 경우
	if body_json is None:
		return False
	#if show:
		# 바디 출력
	#	print(json.dumps(body_json, indent=2))
	# 깔끔한 출력
	pretty_print(body_json, command)
	
	if command == 'enter room':
		global_opposite_user_id = payload['oppositeUserId']
		#print("global_opposite_user_id=" + global_opposite_user_id)
	
	if 'Set-Cookie' in header:
		temp = header['Set-Cookie']
		temp = temp.split(';')[0]
		k, v = temp.split('=')
		print(f'Set-Cookie: key={k}, value={v}')
		global_cookie[k] = v
		if k == 'jwt-access-token':
			global_header['Authorization'] = 'Bearer '+v
	#if 'Authorization' in header:
	#	token = header['Authorization']
	#	print(token)
	#	global_header['Authorization'] = token
	return True


def pretty_print(js, command):
	global global_next_chat_id
	print('=' * 60)
	if command == 'list room':
		for t in js:
			opposite_user_id = t['first']
			chat_id = t['second']
			chat = get_chat_by_id(chat_id)
			print(f"[1:1 채팅방] {opposite_user_id} 와(과)의 대화 / {chat['senderUserId']} : {chat['content']} (보낸 시간 : {chat['created_at']}, 읽은 시간 : {chat['read_at']})")
	elif command in ['enter room', 'list chat by room', 'list all received chat']:
		for chat in js['list']:
			print(f"[id={chat['id']}] {chat['senderUserId']} : {chat['content']} (보낸 시간 : {chat['created_at']}, 읽은 시간 : {chat['read_at']})")
		global_next_chat_id = js['nextId']
	elif command == 'list member':
		for member in js:
			print(f"id={member['id']}, name={member['name']}, status message={member['statusMessage']}")
	elif command in ['login', 'update member info']:
		member = js
		print(f"id={member['id']}, name={member['name']}, status message={member['statusMessage']}")
	return


if __name__ == "__main__":
	print('커맨드를 번호로 입력')
	print('종료할 때는 q 입력')
	while True:
		#print(f'global_header={global_header}')
		#print(f'status={status}')
		#print(f'global_opposite_user_id={global_opposite_user_id}')
		candidate = []
		if status is None:
			candidate = ['signup', 'login', 'quit']
		elif status == 'room list':
			candidate = ['list room', 'enter room', 'new room', 'list member', 'update member', 'logout']
		elif status == 'in room':
			candidate = ['list chat', 'list more chat', 'send chat', 'delete chat', 'exit room']
		
		selected_menu = prompt_command_list(candidate)
		_, command = COMMAND_LIST[selected_menu]
		
		if selected_menu == 'quit':
			exit(0)
		elif selected_menu == 'new room':  # 방 만들고 입장
			print('[유저 리스트]')
			run_command('list member', show=False)
			ret = run_command('enter room')	
		elif command is not None:
			if command == 'send chat' and global_opposite_user_id is not None:
				ret = run_command(command, {'receiverUserId': global_opposite_user_id})
			elif command == 'list chat by room' and global_opposite_user_id is not None:
				payload = {'oppositeUserId': global_opposite_user_id}
				if selected_menu == 'list more chat':
					payload['nextId'] = global_next_chat_id
				ret = run_command(command, payload)
			else:
				ret = run_command(command)
		else:
			ret = True
		
		#print(command, ret)
		if not ret:
			continue
		
		if status is None:
			if selected_menu == 'login':
				status = 'room list'
				print('[방 목록]')
				run_command('list room')
		elif status == 'room list':
			if selected_menu in ['enter room', 'new room']:
				status = 'in room'
			elif selected_menu == 'logout':
				global_cookie.clear()
				global_header.clear()
				status = None
		elif status == 'in room':
			if selected_menu == 'exit room':
				status = 'room list'
				global_opposite_user_id = None
				global_next_chat_id = None
			
		
	
	