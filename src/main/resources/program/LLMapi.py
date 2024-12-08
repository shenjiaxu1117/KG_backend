from pathlib import Path
from openai import OpenAI

import sys
import requests
import json

def kimi(targetFile):

    client = OpenAI(
        api_key="sk-HNzgLCywzciJZhq1k9fND1v39R2lWGXDkHJAVr8i3y0UcDEp",  # 在这里将 MOONSHOT_API_KEY 替换为你从 Kimi 开放平台申请的 API Key
        base_url="https://api.moonshot.cn/v1",
    )

    # moonshot.pdf 是一个示例文件, 我们支持文本文件和图片文件，对于图片文件，我们提供了 OCR 的能力
    # 上传文件时，我们可以直接使用 openai 库的文件上传 API，使用标准库 pathlib 中的 Path 构造文件
    # 对象，并将其传入 file 参数即可，同时将 purpose 参数设置为 file-extract；注意，目前文件上传
    # 接口仅支持 file-extract 一种 purpose 值。
    file_object = client.files.create(file=Path(targetFile), purpose="file-extract")

    # 获取结果
    # file_content = client.files.retrieve_content(file_id=file_object.id)
    # 注意，某些旧版本示例中的 retrieve_content API 在最新版本标记了 warning, 可以用下面这行代替
    # （如果使用旧版本的 SDK，可以继续延用 retrieve_content API）
    file_content = client.files.content(file_id=file_object.id).text

    # 把文件内容通过系统提示词 system prompt 放进请求中
    messages = [
        {
            "role": "system",
            "content": (
                "请你阅读收到的文件，识别文件文本中的实体类型，并提取这些实体的属性，不需要输出具体的实体内容和属性值。根据文件内容，找出实体之间的关系并输出。\n"
                "输出格式如下：\n"
                "实体类型：\n实体类型1（属性1、属性2、属性3...）\n实体类型2（属性1、属性2、属性3...）\n实体类型3（属性1、属性2、属性3...）\n"
                "实体之间的关系：\n实体类型-关系-实体类型\n实体类型-关系-实体类型...\n"
                "输出示例如下：\n"
                "实体类型：\n原告（性别、出生日期、民族、文化程度、工作单位、职务、居住地址）\n被告（性别、出生日期、民族、工作单位、职务、居住地址）\n法院（案件编号）"
                "实体之间的关系：\n审判长-参与-法院\n原告-参与-法院\n"
                "请严格按照以上写明的输出格式输出，只需要归纳实体类型和实体之间的关系，请确保输出每一部分只出现一次，不要输出其他多余内容"
            )
        },
        {
            "role": "system",
            "content": file_content,  # <-- 这里，我们将抽取后的文件内容（注意是文件内容，而不是文件 ID）放置在请求中
        },
        {
            "role": "user",
            "content": "请阅读文件，严格按照指定格式列出实体类型、实体属性和实体之间的关系。"
        },
    ]

    # 然后调用 chat-completion, 获取 Kimi 的回答
    completion = client.chat.completions.create(
        model="moonshot-v1-32k",
        messages=messages,
        temperature=0.3,
    )

    content = completion.choices[0].message.content
    print(content)

def get_access_token():
    """
    使用 API Key，Secret Key 获取access_token，替换下列示例中的应用API Key、应用Secret Key
    """

    url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=6xMtq6a2sT5z2WdUPalkdEg0&client_secret=dNRT1VTLrYbBxRXdctXGW2G8DsfBv7vl"

    # payload = json.dumps("")
    headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }

    response = requests.post(url, headers=headers)
    return response.json().get("access_token")

def wenxin(targetFile):
    url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_speed?access_token=" + get_access_token()

    file = open(targetFile, "r", encoding="utf-8")
    file_content = file.read()
    file.close()

    # 构造消息 payload
    message_content = (
            file_content +
            "\n请你阅读以上内容，识别文本中的实体类型，并提取这些实体的属性，不需要输出具体的实体内容和属性值。根据文本内容，找出实体之间的关系并输出。"
            "\n输出格式如下："
            "\n实体类型：实体类型1（属性1、属性2、属性3...）、实体类型2（属性1、属性2、属性3...）、实体类型3（属性1、属性2、属性3...）"
            "\n实体之间的关系：实体类型-关系-实体类型、实体类型-关系-实体类型..."
            "\n输出示例如下："
            "\n实体类型："
            "原告（性别、出生日期、民族、文化程度、工作单位、职务、居住地址）、"
            "被告（性别、出生日期、民族、工作单位、职务、居住地址）、"
            "法院（案件编号）"
            "\n实体之间的关系："
            "审判长-参与-法院、"
            "原告-参与-法院"
    )

    # 构造 payload
    payload = {
        "messages": [
            {
                "role": "user",
                "content": message_content
            },
        ]
    }

    headers = {
        'Content-Type': 'application/json'
    }

    response = requests.post(url, headers=headers, json=payload)

    print(response.text)



if __name__ == "__main__":
    targetFile = sys.argv[1]  # 获取第一个参数
    kimi(targetFile)
#     kimi("/Users/shenjiaxu/Desktop/实验室/知识图谱/数据集/test/(2001)南民初字第148号民事判决书（2001）民判字第148号.txt")
#     wenxin("/Users/shenjiaxu/Desktop/实验室/知识图谱/数据集/test/(2001)南民初字第148号民事判决书（2001）民判字第148号.txt")