local c = redis.call

local users_cleared_at = tonumber(c("GET", "users_cleared_at")) or -1
local current_time = tonumber(c("TIME")[1])

local result = {}

if users_cleared_at >= current_time - 1 then
    return result
else
    redis.replicate_commands()
    c("SET", "users_cleared_at", tostring(current_time))

    local statuses = c("HGETALL", "users") or {}

    local result = {}

    for idx = 1, #statuses, 2 do
        local id = statuses[idx]
        local status = cjson.decode(statuses[idx + 1])
        local redis_updated_at = tonumber(status["redisUpdatedAt"])

        if redis_updated_at < current_time - 3 then
            c("HDEL", "users", id)
            table.insert(result, id)
        end
    end

    return result
end