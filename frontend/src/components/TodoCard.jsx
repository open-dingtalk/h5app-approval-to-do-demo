import React from 'react'
import { Button } from 'antd-mobile'
import { useNavigate } from 'react-router-dom'
import './TodoCard.css'

const TodoCard = (props) => {
  const { id, title, submiter, reason, todo } = props
  const navigate = useNavigate()

  const handleClick = () => {
    delete todo['forms']
    delete todo['taskId']
    delete todo['url']
    delete todo['instanceId']
    navigate(`/todoDetail/${id}?data=${encodeURIComponent(JSON.stringify(todo))}`)
  }

  return (
    <div className="todo-card">
      <div className="todo-title">{title}</div>
      <div className="user">提交人：{submiter}</div>
      <div className="reason">报销事由：{reason}</div>
      <div className="btn-wrap">
        <Button onClick={() => handleClick()} size="small" color="primary">
          查看详情
        </Button>
      </div>
    </div>
  )
}

export default TodoCard
