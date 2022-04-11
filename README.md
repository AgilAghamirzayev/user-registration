# Spring boot registration app (jwt, email)

# Description
### STEP 0
    you can check the endpoints from swagger 
    http://localhost:8080/swagger-ui-custom.html

### STEP 1
      http://localhost:8080/api/account/signup
        send post request with body :
        {
          "name": "Example",
          "password": "exaple",
          "email": "example@gmail.com"
        }
    
- Note: please use a working email to activate your account

### STEP 2

    Enter your email account and activate your account

### STEP 3
    http://localhost:8080/api/authorize
    send post request with body :
    {
        "email": "example@gmail.com",
        "password": "exaple"
    }

- Note: you check headers, and you can see "Authorization" with token

### STEP 4
    Add the Bearer Token your header (Authorization) and send to 
    http://localhost:8080/api/account/me 
    get request
