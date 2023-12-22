# moka
Bangkit Capstone project - MOKA: Pomodoro Assistant 

# Cloud Computing
- Create endpoints for sign up, login, refresh token, user profile, create task, get all task by status, get task by id, update task, delete task, create history, get all history, get history by id, and delete history
- Tools : NodeJS, ExpressJS, Postman

**Google Cloud Platform Service :**
- Cloud SQL to store data
- Deploy APIs to Cloud Run

**Base URL :**
> https://app-b7r6bjni4q-et.a.run.app

# Sign Up

**Path :**

> /signup

**Method :**

> `POST`

**Request Body :**

> - name as `string`, Name must be at least 3 characters long
> - email as `string`
> - password as `string`, Password must be at least 4 characters long

**Response :**

```json
{
    "status": 201,
    "message": "You have been successfully registered.",
    "user_id": 17
}
```

# Login

**Path :**

> /login

**Method :**

> `POST`

**Request Body :**

> - email as `string`
> - password as `string`

**Response :**

```json
{
    "status": 200,
    "message": "Login successful",
    "access_token": "<token>",
    "refresh_token": "<token>"
}
```

# Refresh Token

**Path :**

> /refresh-token

**Method :**

> `GET`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "status": 200,
    "access_token": "<token>",
    "refresh_token": "<token>"
}
```

# User Profile

**Path :**

> /profile

**Method :**

> `GET`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "status": 200,
    "user": {
        "id": 15,
        "name": "windyfs20",
        "email": "windy123@gmail.com"
    }
}
```

# Create Task

**Path :**

> /task

**Method :**

> `POST`

**Request Body :**

> - taskName as `string`
> - taskDescription as `string`
> - taskDate as `date&time`, empty
> - statusTask as `boolean`, false

**Response :**

```json
{
    "status": 201,
    "message": "Task created successfully",
    "data": {
        "taskId": "Qjd5OEIA24"
    }
}
```

# Get All Task by Status

**Path :**

> /task/status/false

**Method :**

> `GET`

**Response :**

```json
{
    "status": 200,
    "message": "Tasks with status false retrieved successfully",
    "data": [
        {
            "idtask": "kyhbjAbezR",
            "taskname": "tugas deploy api",
            "taskdescription": "sudah dideploy tinggal testing",
            "taskdate": "2023-12-12T00:00:00.000Z",
            "statustask": 0
        }
    ]
}
```

# Get Task By Id

**Path :**

> /task/idtask

**Method :**

> `GET`

**Response :**

```json
{
    "status": 200,
    "message": "Task displayed successfully",
    "data": {
        "idtask": "kyhbjAbezR",
        "taskname": "tugas deploy api",
        "taskdescription": "sudah dideploy tinggal testing",
        "taskdate": "2023-12-12T00:00:00.000Z",
        "statustask": 0
    }
}
```

# Update Task

**Path :**

> /task/idtask

**Method :**

> `PUT`

**Request Body :**

> - taskName as `string`
> - taskDescription as `string`
> - taskDate as `date&time`, empty
> - statusTask as `boolean`, true

**Response :**

```json
{
    "status": 200,
    "message": "Task updated successfully",
    "data": {
        "taskId": "kyhbjAbezR"
    }
}
```

# Delete Task

**Path :**

> /task/idtask

**Method :**

> `DEL`

**Response :**

```json
{
    "status": 200,
    "message": "Task deleted successfully",
    "data": {
        "idtask": "Qjd5OEIA24",
        "taskname": "tugas bangkit 2023",
        "taskdescription": "mengerjakan projek capstone",
        "taskdate": "2023-12-13T00:00:00.000Z",
        "statustask": 0
    }
}
```

# Create History

**Path :**

> /history

**Method :**

> `POST`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Request Body :**

> - taskId as `string`

**Response :**

```json
{
"error": false,
"message": "success"
}
```

# Get All History

**Path :**

> /history

**Method :**

> `GET`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "status": 200,
    "message": "All history records displayed successfully",
    "data": []
}
```

# Get History By Id

**Path :**

> /history/idhistory

**Method :**

> `GET`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "status": 200,
    "message": "History record with id pIW-V6nAm8 retrieved successfully",
    "data": {
        "idhistory": "pIW-V6nAm8",
        "idtask": "kyhbjAbezR",
        "taskname": "tugas bangkit 2023",
        "taskdescription": "mengerjakan projek capstone",
        "taskdate": "2023-12-12T00:00:00.000Z",
        "statustask": 1
    }
}
```

# Delete History

**Path :**

> /history/idhistory

**Method :**

> `DEL`

**Request Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "status": 200,
    "message": "History record deleted successfully",
    "data": {
        "idhistory": "pIW-V6nAm8",
        "idtask": "kyhbjAbezR"
    }
}
```