const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require('socket.io')
const io = new Server(server)
const port = process.env.PORT || 8080
const cors = require('cors')

app.use(cors())

app.get('/', (req, res) => {
  res.send('socket.io is running!')
})

io.on('connection', (socket) => {
  console.log('a user connected')
  socket.on('auth', (username, password) => {
  	console.log('auth')
  	if (username === password) {
  		socket.emit('auth_ok')
  	}
  })
  socket.on('disconnect', () => {
  	console.log('a user disconnected')
  })
})

server.listen(port, () => {
  console.log(`listening on *:${port}`)
})
