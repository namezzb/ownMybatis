package cn.zzb.mybatis.test.dao;

public interface IHostpitalDao {
    Integer selectPatientGenderById(Integer patientId);
    String selectPatientNameById(Integer patientId);
}
