/**
 *  Created by pw on 2021/11/24 下午5:54.
 */
import React, { useState, useContext, useEffect } from 'react'
import { Form, Input, Button, CascadePicker, NavBar, Dialog } from 'antd-mobile'
import './index.css'
import { useNavigate } from 'react-router-dom'
import { UserInfoContext } from '../../App'
import axios from 'axios'
import short from 'short-uuid'

const options1 = [
  {
    label: '申请单',
    value: '申请单'
  },
  {
    label: '报销单',
    value: '报销单'
  },
  {
    label: '借款单',
    value: '借款单'
  }
]
// const person = [['张三', '李四', '王五', '赵六']]
const Main = () => {
  const context = useContext(UserInfoContext)
  console.log('-----', context)
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [visible, setVisible] = useState(false)
  const [loading, setLoading] = useState(false)
  const [value, setValue] = useState('申请单')
  const [type, setType] = useState('flow')
  const [options, secOptions] = useState(options1)
  const [person, setPerson] = useState([])
  const [value1, setValue1] = useState('')

  const back = () => {
    navigate('/', { replace: true })
  }
  const request = (values) => {
    setLoading(true)
    let temp = { ...values, userName: value1 }
    const uuid = short.generate()
    axios
      .post(context.domain + '/biz/createTask', {
        ...temp,
        uuid,
        url: `/#/bill?data=${encodeURIComponent(JSON.stringify(temp))}&uuid=${uuid}`
      })
      .then((res) => {
        setLoading(false)
        if (res && res.data.success) {
          Dialog.alert({
            content: '提交单据成功!',
            onConfirm: () => {
              back()
            }
          })
        } else {
          alert('request failed --->' + JSON.stringify(res))
        }
      })
      .catch((error) => {
        setLoading(false)
        alert('httpRequest failed --->' + JSON.stringify(error))
      })
  }
  const onSubmit = async () => {
    try {
      const values = await form.validateFields()
      console.log(values)
      request(values)
    } catch (errorList) {
      console.log(errorList)
      // errorList.forEach(({ name, errors }) => {
      //   // Do something...
      // })
    }
  }
  const getList = () => {
    axios
      .post(context.domain + '/biz/query/user/list')
      .then((res) => {
        if (res && res.data.success) {
          const data = res?.data?.data
          const pp = data.map((e) => {
            return { label: e?.name, value: e?.userid }
          })
          setPerson(pp)
        } else {
          alert('request failed --->' + JSON.stringify(res))
        }
      })
      .catch((error) => {
        alert('httpRequest failed --->' + JSON.stringify(error))
      })
  }
  useEffect(() => {
    form.setFieldsValue({
      flowTitleName: '申请单'
    })
    getList()
  }, [])

  return (
    <div className="apply">
      <div className="header">
        <NavBar onBack={back}>{value}</NavBar>
      </div>
      <div className="form">
        <Form
          form={form}
          footer={
            <div className="btns">
              <Button block type="submit" color="primary" onClick={onSubmit} loading={loading}>
                提交送审
              </Button>
            </div>
          }
        >
          <Form.Item
            name="flowTitleName"
            label="单据模板"
            layout="horizontal"
            rules={[{ required: true, message: '单据模板为空' }]}
          >
            <div
              onClick={() => {
                secOptions(options1)
                setType('flowTitleName')
                !visible && setVisible(true)
              }}
            >
              <Input placeholder="请选单据模板" value={value} disabled />
            </div>
          </Form.Item>
          <Form.Item
            name="flowName"
            label={`${value?.substring(0, value?.length - 1) || '报销'}单名字`}
            rules={[{ required: true, message: `${value?.substring(0, value?.length - 1) || '报销'}单名字不能为空` }]}
          >
            <Input placeholder={`请输入${value?.substring(0, value?.length - 1) || '报销'}单名字`} />
          </Form.Item>
          <Form.Item
            name="userId"
            label={`${value?.substring(0, value?.length - 1) || '报销'}人`}
            rules={[{ required: true, message: `${value?.substring(0, value?.length - 1) || '报销'}人不能为空` }]}
          >
            <div
              onClick={() => {
                if (person?.length > 0) {
                  secOptions(person)
                  setType('userId')
                  !visible && setVisible(true)
                } else {
                  getList()
                }
              }}
            >
              <Input
                placeholder={`请选择${value?.substring(0, value?.length - 1) || '报销'}人`}
                value={value1}
                disabled
              />
            </div>
          </Form.Item>
          <Form.Item
            name="flowDesc"
            label={`${value?.substring(0, value?.length - 1) || '报销'}事由`}
            rules={[{ required: true, message: `${value?.substring(0, value?.length - 1) || '报销'}事由不能为空` }]}
          >
            <Input placeholder={`请输入${value?.substring(0, value?.length - 1) || '报销'}事由`} />
          </Form.Item>
          <Form.Item
            name="flowAmount"
            label={`${value?.substring(0, value?.length - 1) || '报销'}金额`}
            rules={[{ required: true, message: `${value?.substring(0, value?.length - 1) || '报销'}金额不能为空` }]}
          >
            <Input placeholder={`请输入${value?.substring(0, value?.length - 1) || '报销'}金额`} />
          </Form.Item>
        </Form>
        <CascadePicker
          options={options}
          visible={visible}
          onClose={() => {
            setVisible(false)
          }}
          value={value}
          onConfirm={(v) => {
            console.log('--------', v)
            if (type === 'flowTitleName') {
              setValue(v[0])
            } else {
              const e = person.find((e) => e?.value === v[0])
              setValue1(e.label)
            }
            form.setFieldsValue({ [type]: v[0] })
          }}
        />
      </div>
    </div>
  )
}
export default Main
