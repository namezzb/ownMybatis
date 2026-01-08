package cn.zzb.mybatis.test.dao;

public interface IUserDao {

    Integer selectUserGenderById(Integer userId);

    String selectUserNameById(Integer userId);
}
