# 拼音搜索-Rework
这个是gtnh版本的拼音搜索</br>
通过反编译云佬写的代码,重新编写了这个mod,在nei中添加了更多的选项
支持自定义lua脚本 会在运行的游戏目录下生成pinin的文件夹,你只需要再里面写lua代码就好了
下面是示例
```lua
function text_handler(s)
-- 如果你不做任何操作则返回原来的文本 不能为空
  return s
end

function input_handler(s)
-- 返回字符串表,java会将表内所有的str对文本进行and运算,所有的结果为true则匹配成功,如果你不想做任何操作则返回nil或者空的数组
  return nil
end

function name()
-- 返回脚本名字,方便在nei里面单独启用或者关闭
  return ""
end
```
