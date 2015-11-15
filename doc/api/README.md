## 安全检查信息管理服务器接口文档
##

### 目录
1. 介绍
1. 接口描述
1. 安全检查信息管理接口
1. 服务器接口调用错误码
1. 附录

##
### 介绍
安全检查信息管理服务器后台采用jfinal,数据库采用mysql,客户端与服务器之间数据传递格式采用json。

##
### 接口描述
- 接口使用时，将调用地址中的BaseUrl替换成IP地址或域名+端口地址(默认为80)。
- 接口调用成功时返回JSONObject对象。
- 接口调用可用GET或POST方法,当使用POST方法时,需要传递JSONObject对象的body。
- JSONObject对象数据结构如下：
<pre>
{
	report_name:"AH-H001-01 2015年3月11日18点测试表",
	report_creator:"黄金定",
	report_crete_time:"2015-11-14 11:46:10",
	contents:
	[{
		No:"1",
		content:"劳动防护用品",
		items:
		[{
			item:"1.1按规定穿戴安全帽、工作服、劳保鞋等劳保用品，并保持整洁",
			passed："1",
			note:"",
			picUrl:""
		}]
			
	},
	{
		No:"2",
		content:"防雷防静电",
		items:
		[{
			item:"2.1按规定进行防雷防静电检测，并提供检测记录",
			passed："1",
			note:"",
			picUrl:""
		},
		{
			item:"2.2接地线、网无破损、断裂",
			passed："1",
			note:"",
			picUrl:""
		}
		]
			
	}
}
</pre>

##
### 安全检查信息查询
	GET http://BaseUrl/report/query?report_id=1

#### 请求参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_id 	|String		 |必选		  | 信息表的id

#### 响应参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_name |String		 |必有		  | 信息表的名字
report_creator |String		 |必有		  | 信息表的创建者
report_create_time |String		 |必有		  | 信息表的创建时间
contents |Object		 |必有		  | 信息表的检查内容
No |String		 |必有		  | 信息表的检查内容的序号
content |String		 |必有		  | 信息表的检查内容的名字
items |String		 |必有		  | 信息表的一个检查内容的所有检查项
item |String		 |必有		  | 信息表的检查项
passed |String		 |必有		  | 信息表的检查项是否通过，0 为未通过，1 未通过
note |String		 |必有		  | 信息表的检查项备注，空字符串表示没有
picUrl |String		 |必有		  | 信息表的检查项图片，空字符串表示没有

#### 响应成功的结果示例

	{
		report_name:"AH-H001-01 2015年3月11日18点测试表",
		report_creator:"黄金定",
		report_crete_time:"2015-11-14 11:46:10",
		contents:
		[{
			No:"1",
			content:"劳动防护用品",
			items:
			[{
				item:"1.1按规定穿戴安全帽、工作服、劳保鞋等劳保用品，并保持整洁",
				passed："1",
				note:"",
				picUrl:""
			}]
				
		},
		{
			No:"2",
			content:"防雷防静电",
			items:
			[{
				item:"2.1按规定进行防雷防静电检测，并提供检测记录",
				passed："1",
				note:"",
				picUrl:""
			},
			{
				item:"2.2接地线、网无破损、断裂",
				passed："1",
				note:"",
				picUrl:""
			}
			]
				
		}
	}

##
### 安全检查信息录入
	POST http://BaseUrl/report/update?type=add

#### 请求参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_name |String		 |必有		  | 信息表的名字
report_creator |String		 |必有		  | 信息表的创建者
report_create_time |String		 |必有		  | 信息表的创建时间
contents |Object		 |必有		  | 信息表的检查内容
No |String		 |必有		  | 信息表的检查内容的序号
content |String		 |必有		  | 信息表的检查内容的名字
items |String		 |必有		  | 信息表的一个检查内容的所有检查项
item |String		 |必有		  | 信息表的检查项
passed |String		 |必有		  | 信息表的检查项是否通过，0 为未通过，1 未通过
note |String		 |必有		  | 信息表的检查项备注，空字符串表示没有
picUrl |String		 |必有		  | 信息表的检查项图片，空字符串表示没有

#### 请求body示例
	{
			report_name:"AH-H001-01 2015年3月11日18点测试表",
			report_creator:"黄金定",
			report_crete_time:"2015-11-14 11:46:10",
			contents:
			[{
				No:"1",
				content:"劳动防护用品",
				items:
				[{
					item:"1.1按规定穿戴安全帽、工作服、劳保鞋等劳保用品，并保持整洁",
					passed："1",
					note:"",
					picUrl:""
				}]
					
			},
			{
				No:"2",
				content:"防雷防静电",
				items:
				[{
					item:"2.1按规定进行防雷防静电检测，并提供检测记录",
					passed："1",
					note:"",
					picUrl:""
				},
				{
					item:"2.2接地线、网无破损、断裂",
					passed："1",
					note:"",
					picUrl:""
				}
				]
					
			}
		}


#### 响应参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
status 	|String		 |必选		  | 信息表的操作的状态，SUCCESS或FAILED

#### 响应成功的结果示例
	{
		status : "SUCCESS"
	}

##
### 安全检查信息删除
	GET http://BaseUrl/report/query?type=delete&report_id=1

#### 请求参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
type 	|String		 |必选		  | 信息表的操作的类型,此处为delete
report_id 	|String		 |必选		  | 信息表的id

#### 响应参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
status 	|String		 |必选		  | 信息表的操作的状态，SUCCESS或FAILED

#### 响应成功的结果示例
	{
		status : "SUCCESS"
	}

##
### 安全检查信息修改
	POST http://BaseUrl/report/update?type=edit&report_id=1

#### 请求参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
report_name |String		 |必有		  | 信息表的名字
report_creator |String		 |必有		  | 信息表的创建者
report_create_time |String		 |必有		  | 信息表的创建时间
contents |Object		 |必有		  | 信息表的检查内容
No |String		 |必有		  | 信息表的检查内容的序号
content |String		 |必有		  | 信息表的检查内容的名字
items |String		 |必有		  | 信息表的一个检查内容的所有检查项
item |String		 |必有		  | 信息表的检查项
passed |String		 |必有		  | 信息表的检查项是否通过，0 为未通过，1 未通过
note |String		 |必有		  | 信息表的检查项备注，空字符串表示没有
picUrl |String		 |必有		  | 信息表的检查项图片，空字符串表示没有
report_id 	|String		 |必选		  | 信息表的id
type 	|String		 |必选		  | 信息表的操作的类型,此处为edit

#### 响应参数说明
属性			|类型		 |约束		  | 说明
------------|------------|------------|------------
status 	|String		 |必选		  | 信息表的操作的状态，SUCCESS或FAILED

#### 响应成功的结果示例
	{
		status : "SUCCESS"
	}