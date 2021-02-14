package br.com.legion.glory.points;

import java.util.Map;

public class GloryPointsAPI {

    public static Double get(String name) {
        Double points = GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().get(name);
        if (points == null) {
            return 0.0;
        }

        return points;
    }

    public static void deposit(String name, double points) {
        Double points0 = GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().get(name);
        if (points0 == null) {
            points0 = 0.0;
        }

        GloryPointsProvider.Repositories.GLORY_POINTS.provide().insertOrUpdate(name, points0 + points);
        GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().invalidate(name);
    }

    public static void withdraw(String name, double points) {
        Double points0 = GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().get(name);
        if (points0 == null) {
            points0 = 0.0;
        }

        double newBalance = points0 - points;
        if (newBalance < 0.0) {
            newBalance = 0.0;
        }

        GloryPointsProvider.Repositories.GLORY_POINTS.provide().insertOrUpdate(name, newBalance);
        GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().invalidate(name);
    }

    public static void define(String name, double points) {
        GloryPointsProvider.Repositories.GLORY_POINTS.provide().insertOrUpdate(name, points);
        GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().invalidate(name);
    }

    public static boolean has(String name, double points) {
        Double points0 = GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().get(name);
        if (points0 == null) {
            return false;
        }

        return points0 >= points;
    }

    public static Map<String, Double> getTop(int index) {
        return GloryPointsProvider.Repositories.GLORY_POINTS.provide().fetchTop(index);
    }
}
