package br.com.legion.guilds.framework;

import br.com.legion.guilds.framework.server.ServerType;
import com.google.common.base.Enums;

public class GuildsFrameworkConstants {

    public static final ServerType SERVER_TYPE = Enums.getIfPresent(ServerType.class,
            GuildsFrameworkPlugin.getInstance().getConfig().getString("server-type")).orNull();
}
