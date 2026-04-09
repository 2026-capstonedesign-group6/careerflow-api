# 프론트엔드 인증 가이드

## 공통 사항

### 응답 형식

```json
{
  "result": "SUCCESS",
  "code": "200",
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { ... },
  "timestamp": "2026-04-10 12:00:00"
}
```

실패 시 `result: "FAIL"`, `code`에 에러 코드가 담깁니다.

### 토큰 구조

| 토큰 | 저장 위치 | 유효 시간 | 관리 주체 |
|------|-----------|-----------|-----------|
| Access Token | 메모리 or localStorage | 30분 | 프론트 직접 관리 |
| Refresh Token | httpOnly 쿠키 | 14일 | 브라우저 자동 관리 |

> Refresh Token은 httpOnly 쿠키라 JS에서 접근 불가합니다. 모든 요청에 `credentials: "include"` 만 붙이면 자동으로 전송됩니다.

---

## 1. 로컬 회원가입

### 플로우

```
[회원가입 폼 제출]
      ↓
POST /api/auth/signup/local
      ↓ 성공 (201)
[인증 코드 입력 화면으로 이동]
      ↓ 이메일에서 6자리 코드 확인
POST /api/auth/verify/email
      ↓ 성공 (200)
[로그인 화면으로 이동]
```

> 회원가입 요청 시 유저가 DB에 저장되지 않습니다.
> 인증 완료 시점에 유저가 생성됩니다.
> 인증 코드는 **10분** 안에 입력해야 합니다.

---

### API

#### 회원가입

```
POST /api/auth/signup/local
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "nickname": "홍길동",
  "password": "password1234!"
}
```

**에러 코드**

| 코드 | 상황 | 처리 |
|------|------|------|
| U001 | 이미 가입된 이메일 | "이미 사용 중인 이메일입니다." |
| U002 | 이미 사용 중인 닉네임 | "이미 사용 중인 닉네임입니다." |
| A009 | 동일 이메일로 5회 초과 시도 | "잠시 후 다시 시도해 주세요. (10분)" |
| G004 | 입력값 형식 오류 | `data` 배열에 필드별 오류 메시지 |

---

#### 이메일 인증 코드 확인

```
POST /api/auth/verify/email
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "code": "483921"
}
```

**에러 코드**

| 코드 | 상황 | 처리 |
|------|------|------|
| A014 | 코드 만료 (10분 초과) | "인증 시간이 만료되었습니다. 재발송해 주세요." |
| A015 | 코드 불일치 | "인증 코드가 올바르지 않습니다." |
| A013 | 이미 인증 완료 | 로그인 화면으로 이동 |

---

#### 인증 코드 재발송

```
POST /api/auth/verify/email/resend
Content-Type: application/json
```

```json
{
  "email": "user@example.com"
}
```

---

### 구현 예시 (React)

```jsx
// 회원가입 페이지
const SignupPage = () => {
  const [step, setStep] = useState("form"); // "form" | "verify"
  const [email, setEmail] = useState("");

  const handleSignup = async (formData) => {
    const res = await fetch("/api/auth/signup/local", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formData),
    });

    const { result, code, message } = await res.json();

    if (result === "FAIL") {
      if (code === "A009") alert("잠시 후 다시 시도해 주세요. (10분)");
      else alert(message);
      return;
    }

    setEmail(formData.email);
    setStep("verify"); // 인증 코드 입력 화면으로 전환
  };

  const handleVerify = async (code) => {
    const res = await fetch("/api/auth/verify/email", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, code }),
    });

    const { result, code: errorCode, message } = await res.json();

    if (result === "FAIL") {
      if (errorCode === "A014") alert("인증 시간이 만료되었습니다. 코드를 재발송해 주세요.");
      else alert(message);
      return;
    }

    navigate("/login"); // 로그인 화면으로 이동
  };

  const handleResend = async () => {
    await fetch("/api/auth/verify/email/resend", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });
    alert("인증 코드를 재발송했습니다.");
  };
};
```

---

## 2. 로컬 로그인

```
POST /api/auth/login/local
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "password1234!"
}
```

**성공 응답**

```json
{
  "result": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGci..."
  }
}
```

**에러 코드**

| 코드 | 상황 |
|------|------|
| U004 | 존재하지 않는 이메일 |
| A012 | 비밀번호 불일치 |

---

### 구현 예시 (React)

```jsx
const handleLogin = async ({ email, password }) => {
  const res = await fetch("/api/auth/login/local", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
    credentials: "include", // refreshToken 쿠키 수신
  });

  const { result, data, message } = await res.json();

  if (result === "FAIL") {
    alert(message);
    return;
  }

  // Access Token 저장
  localStorage.setItem("accessToken", data.accessToken);
  navigate("/");
};
```

---

## 3. 소셜 로그인 (카카오 / 구글)

### 플로우

```
[소셜 로그인 버튼 클릭]
      ↓
Provider 로그인 페이지로 이동
      ↓ 로그인 완료
프론트 콜백 페이지로 리다이렉트
http://localhost:3000/oauth2/callback/kakao?code=xxxxx
      ↓
POST /api/auth/oauth2/callback/kakao  { code }
      ↓ 성공 (200)
[메인 페이지로 이동]
```

> 신규 유저는 자동으로 회원가입됩니다. (이메일 인증 불필요)

---

### API

#### 로그인 버튼 클릭 시

```javascript
// 카카오
window.location.href = "http://localhost:8080/api/auth/oauth2/authorization/kakao";

// 구글
window.location.href = "http://localhost:8080/api/auth/oauth2/authorization/google";
```

---

#### 콜백 페이지 (`/oauth2/callback/:provider`)

```
POST /api/auth/oauth2/callback/{provider}
Content-Type: application/json
```

```json
{
  "code": "authorization_code_from_provider"
}
```

**에러 코드**

| 코드 | 상황 |
|------|------|
| A010 | 소셜 로그인 실패 (code 만료 등) |
| A011 | 지원하지 않는 Provider |
| U001 | 동일 이메일로 이미 로컬 또는 다른 소셜로 가입됨 |

---

### 구현 예시 (React)

```jsx
// /oauth2/callback/:provider 페이지
const OAuthCallbackPage = () => {
  const { provider } = useParams();

  useEffect(() => {
    const code = new URLSearchParams(window.location.search).get("code");
    if (!code) return navigate("/login");

    const login = async () => {
      const res = await fetch(`/api/auth/oauth2/callback/${provider}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code }),
        credentials: "include", // refreshToken 쿠키 수신
      });

      const { result, data, message } = await res.json();

      if (result === "FAIL") {
        alert(message);
        navigate("/login");
        return;
      }

      localStorage.setItem("accessToken", data.accessToken);
      navigate("/");
    };

    login();
  }, []);

  return <div>로그인 처리 중...</div>;
};
```

---

## 4. 토큰 관리

### Access Token 재발급 (401 발생 시)

```
POST /api/auth/refresh
credentials: "include"
```

**성공 응답**

```json
{
  "data": { "accessToken": "eyJhbGci..." }
}
```

**에러 코드**

| 코드 | 상황 | 처리 |
|------|------|------|
| A020 | Refresh Token 유효하지 않음 | 로그인 화면으로 이동 |
| A021 | Refresh Token 없음 (로그아웃 상태) | 로그인 화면으로 이동 |
| A023 | 토큰 탈취 감지 | 로그인 화면으로 이동 |

---

### 로그아웃

```
POST /api/auth/logout
credentials: "include"
```

서버에서 Refresh Token 쿠키를 삭제합니다.

```javascript
const handleLogout = async () => {
  await fetch("/api/auth/logout", {
    method: "POST",
    credentials: "include",
  });
  localStorage.removeItem("accessToken");
  navigate("/login");
};
```

---

### API 요청 유틸 (자동 토큰 갱신)

Access Token 만료(401) 시 자동으로 재발급 후 재시도하는 유틸 함수입니다.

```javascript
// api.js
const BASE_URL = "http://localhost:8080";

export const api = async (url, options = {}) => {
  const accessToken = localStorage.getItem("accessToken");

  const res = await fetch(BASE_URL + url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
      ...options.headers,
    },
    credentials: "include",
  });

  // 401이면 토큰 재발급 후 재시도
  if (res.status === 401) {
    const refreshed = await refreshAccessToken();
    if (!refreshed) {
      localStorage.removeItem("accessToken");
      window.location.href = "/login";
      return;
    }

    return api(url, options); // 재시도
  }

  return res.json();
};

const refreshAccessToken = async () => {
  const res = await fetch(BASE_URL + "/api/auth/refresh", {
    method: "POST",
    credentials: "include",
  });

  if (!res.ok) return false;

  const { data } = await res.json();
  localStorage.setItem("accessToken", data.accessToken);
  return true;
};
```

**사용 예시**

```javascript
// 인증이 필요한 API 호출
const profile = await api("/api/user/profile");
const result = await api("/api/posts", {
  method: "POST",
  body: JSON.stringify({ title: "제목", content: "내용" }),
});
```