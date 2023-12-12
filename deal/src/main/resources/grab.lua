--1.参数列表
--1.1 优惠券id，前半部分是固定的，后半是动态的
local redBagId = ARGV[1]

local userId = ARGV[2]
--2.数据key
--2.1 红包key
local redBagKey = 'redBag:' .. redBagId
--2.2 余额key
local amountKey = 'redBag:amount:' .. redBagId

local grabKey = 'redBag:grab:' .. redBagId

local pop = redis.call('rpop',redBagKey)

local amount = redis.call('get',amountKey)

if redis.call("ismember", grabKey, userId) then
  return 0
end

--3.脚本业务

if(pop == nil) then
    return 0
end

if(amount-pop==0) then
    redis.call("del",redBagKey)
    redis.call("del",grabKey)
else
    redis.call("set",amountKey,amount-pop)
    redis.call("sadd",grabKey,userId)
end


return pop