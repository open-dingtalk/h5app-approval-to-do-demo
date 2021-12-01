/**
 *  Created by pw on 2021/11/24 下午5:54.
 */
import React, { useState, useEffect, useContext } from 'react'
import { Form, Input, Button, Picker, NavBar, Dialog, TextArea, Toast } from 'antd-mobile'
import './index.css'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { UserInfoContext } from '../../App'
import axios from 'axios'
function rand(min, max) {
  return (Math.random() * (max - min + 1) + min) | 0
}
const billColumns = [['申请单', '报销单', '借款单']]
const approveColumns = [['审批流A', '审批流B', '审批流C', '审批流D', '审批流E']]
const person = ['张三', '李四', '王五', '赵六']
const applyReason = ['申请去北京的出差报销', '报销 4 月份的办公室费用', '6 月份团建费用报销']
const applyMoney = [12, 23, 200, 400, 666]
const Main = () => {
  const navigate = useNavigate()
  const context = useContext(UserInfoContext)
  const [searchParams, setSearchParams] = useSearchParams()
  const [form] = Form.useForm()
  const [visible, setVisible] = useState(false)
  const [columns, secColumns] = useState(billColumns)
  const [type, setType] = useState('flow')
  const [loading, setLoading] = useState(false)
  const [loading1, setLoading1] = useState(false)
  const [workRecordId, setWorkRecordId] = useState('')
  const [value, setValue] = useState(billColumns[0][rand(0, billColumns[0].length - 1)])
  const [value1, setValue1] = useState(approveColumns[0][rand(0, approveColumns[0].length - 1)])

  const back = () => {
    navigate('/', { replace: true })
  }
  const onSubmit = (type) => {
    type === 'agree' ? setLoading(true) : setLoading1(true)
    axios
      .post(context.domain + '/biz/update', {
        userId: context.userId,
        userName: context.userName,
        workRecordId: workRecordId,
        flowStatus: type
      })
      .then((res) => {
        type === 'agree' ? setLoading(false) : setLoading1(false)
        if (res && res.data.success) {
          Dialog.alert({
            content: `${type === 'agree' ? '同意' : '驳回'}成功!`,
            onConfirm: () => {
              back()
            }
          })
        } else {
          alert('request failed --->' + JSON.stringify(res))
        }
      })
      .catch((error) => {
        type === 'agree' ? setLoading(false) : setLoading1(false)
        alert('httpRequest failed --->' + JSON.stringify(error))
      })
  }
  useEffect(() => {
    const str = searchParams.get('data')
    const uuid = searchParams.get('uuid')
    axios
      .post(context.domain + '/biz/query/uuid', { uuid })
      .then((res) => {
        if (res && res.data.success) {
          setWorkRecordId(res.data.data)
        } else {
          alert('request failed --->' + JSON.stringify(res))
        }
      })
      .catch((error) => {
        type === 'agree' ? setLoading(false) : setLoading1(false)
        alert('httpRequest failed --->' + JSON.stringify(error))
      })
    console.log('routeParams', str)
    try {
      const data = JSON.parse(str)
      form.setFieldsValue({
        flow: data?.['flowTitleName'] || '申请单',
        approve: 'ww',
        person: data?.['userName'] || person[rand(0, person.length - 1)],
        applyReason: data?.['flowDesc'] || applyReason[rand(0, applyReason.length - 1)],
        applyMoney: data?.['flowAmount'] || applyMoney[rand(0, applyMoney.length - 1)]
      })
      data?.['flowTitleName'] && setValue(data?.['flowTitleName'])
    } catch (error) {
      form.setFieldsValue({
        flow: '申请单',
        approve: 'ww',
        person: person[rand(0, person.length - 1)],
        applyReason: applyReason[rand(0, applyReason.length - 1)],
        applyMoney: applyMoney[rand(0, applyMoney.length - 1)]
      })
    }
  }, [])
  return (
    <div className="bill">
      <div className="header">
        <NavBar onBack={back}>{value}</NavBar>
      </div>
      <div className="form">
        <Form
          form={form}
          footer={
            <div className="btns">
              <Button
                block
                type="submit"
                disabled={!workRecordId}
                loading={loading}
                color="primary"
                onClick={() => onSubmit('agree')}
              >
                同意
              </Button>
              <Button
                block
                type="submit"
                color="danger"
                loading={loading1}
                disabled={!workRecordId}
                style={{ marginLeft: '20px' }}
                onClick={() => onSubmit('refuse')}
              >
                驳回
              </Button>
            </div>
          }
        >
          <Form.Item name="flow" label="单据模板" layout="horizontal">
            <div
              onClick={() => {
                secColumns(billColumns)
                setType('flow')
                // !visible && setVisible(true)
              }}
            >
              <Input placeholder="请选单据模板" value={value} disabled />
            </div>
          </Form.Item>
          <Form.Item name="approve" label="审批流">
            <div
              onClick={() => {
                secColumns(approveColumns)
                setType('approve')
                // !visible && setVisible(true)
              }}
            >
              <Input placeholder="请选择审批流" value={value1} disabled />
            </div>
          </Form.Item>
          <Form.Item name="person" label={`${value?.substring(0, value?.length - 1) || '报销'}人`}>
            <Input placeholder="请输入报销人" disabled />
          </Form.Item>
          <Form.Item name="applyReason" label={`${value?.substring(0, value?.length - 1) || '报销'}事由`}>
            <Input placeholder="请输入报销事由" disabled />
          </Form.Item>
          <Form.Item name="applyMoney" label={`${value?.substring(0, value?.length - 1) || '报销'}金额`}>
            <Input placeholder="请输入报销金额" type="number" disabled />
          </Form.Item>
        </Form>
        <Picker
          columns={columns}
          visible={visible}
          onClose={() => {
            setVisible(false)
          }}
          value={value}
          onConfirm={(v) => {
            type === 'flow' ? setValue(v[0]) : setValue1(v[0])
            form.setFieldsValue({ [type]: v[0] })
          }}
        />
      </div>
    </div>
  )
}
export default Main
