package banking.connection.operation;

import java.util.List;

public interface Operation<O> {
  List<O> readByCriteria(int option, String... criteria);

  void create(O object);

  void delete(O object);

  void deleteAll();

  void transfer(O originator, O beneficiary, int amount);
}
