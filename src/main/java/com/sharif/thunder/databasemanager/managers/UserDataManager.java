package com.sharif.thunder.databasemanager.managers;

import com.sharif.thunder.database.DataManager;
import com.sharif.thunder.database.DatabaseConnector;
import com.sharif.thunder.database.SQLColumn;
import com.sharif.thunder.database.columns.IntegerColumn;
import com.sharif.thunder.database.columns.LongColumn;
import net.dv8tion.jda.api.entities.User;

public class UserDataManager extends DataManager {

  public static final SQLColumn<Long> USER_ID = new LongColumn("USER_ID", false, 0L);
  public static final SQLColumn<Integer> MONEY = new IntegerColumn("MONEY", false, 0);

  public UserDataManager(DatabaseConnector connector) {
    super(connector, "USERDATA");
  }

  @Override
  protected String primaryKey() {
    return "" + USER_ID + "";
  }

  public int[] addMoney(long targetId, int value) {
    return readWrite(
        selectAll(USER_ID.is(targetId)),
        rs -> {
          if (rs.next()) {
            int current = MONEY.getValue(rs);
            MONEY.updateValue(rs, current + value < 0 ? 0 : current + value);
            rs.updateRow();
            return new int[] {current, current + value < 0 ? 0 : current + value};
          } else {
            rs.moveToInsertRow();
            USER_ID.updateValue(rs, targetId);
            MONEY.updateValue(rs, value < 0 ? 0 : value);
          }
        });
  }

  public int[] removeMoney(User target, int value) {
    return removeMoney(target.getIdLong(), value);
  }

  public int[] removeMoney(long targetId, int value) {
    return addMoney(targetId, -1 * value);
  }

  public int getMoney(User target) {
    return getMoney(target.getIdLong());
  }

  public int getMoney(long targetId) {
    return read(
        selectAll(USER_ID.is(targetId)),
        rs -> {
          if (rs.next()) {
            return MONEY.getValue(rs);
            return 0;
          }
        });
  }
}
