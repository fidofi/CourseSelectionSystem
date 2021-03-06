package com.fidofi.dao.impl;

import com.fidofi.constant.CreditConstant;
import com.fidofi.dao.CourseDao;
import com.fidofi.entity.Category;
import com.fidofi.entity.Course;
import com.fidofi.entity.Page;
import com.fidofi.entity.Student;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by fido on 2017/12/16.
 */
@Repository
@Transactional
public class CourseDaoImpl extends HibernateDaoSupport implements CourseDao {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    /**
     * 新增课程
     *
     * @param course
     */
    public void create(Course course) {
        this.getCurrentSession().save(course);

    }

    /**
     * 更新课程
     *
     * @param course
     * @return
     */
    public Course update(Course course) {
        this.getCurrentSession().update(course);
        Course newCourse = this.selectByCourseId(course.getCourseId());
        return newCourse;
    }

    /**
     * 删除课程
     *
     * @param courseId
     */
    public void delete(Integer courseId) {
        String hql = "delete from Course where courseId=:n ";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("n", courseId);
        query.executeUpdate();
    }

    /**
     * 分页查询课程
     *
     * @param page
     * @return
     */
    public List<Course> selectCourses(Page page) {
        String hql = null;
        if (page.getOrderRole().equals("desc"))
            hql = "from Course order by credit desc";
        else
            hql = "from Course order by credit asc";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        List<Course> courseList = query.list();
        return courseList;
    }

    /**
     * 根据授课老师姓名查询
     *
     * @param page
     * @param teacherName
     * @return
     */
    public List<Course> selectByTeacher(Page page, String teacherName) {
        String hql = "from Course where teacherName=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        //条件查询
        query.setParameter("n", teacherName);
        //分页设置
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        List<Course> courseList = query.list();
        return courseList;

    }

    /**
     * 根据课程类别查询
     *
     * @param page
     * @param categoryId
     * @return
     */
    public List<Course> selectByCategory(Page page, Integer categoryId) {
        String hql = "from Course where categoryId=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        //条件查询
        query.setParameter("n", categoryId);
        //分页设置
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        List<Course> courseList = query.list();
        return courseList;
    }

    /**
     * 根据课程名称查询
     *
     * @param page
     * @param courseName
     * @return
     */
    public List<Course> selectByCourseName(Page page, String courseName) {
        String hql = null;
        if (page.getOrderRole().equals("desc"))
            hql = "from Course where courseName=:n order by credit desc";
        else
            hql = "from Course where courseName=:n order by credit asc";
        Query query = this.getCurrentSession().createQuery(hql);
        //条件查询
        query.setParameter("n", courseName);
        //分页设置
        query.setFirstResult(page.getStartIndex());
        query.setMaxResults(page.getPageSize());
        List<Course> courseList = query.list();
        return courseList;
    }

    /**
     * 取消选课
     *
     * @param courseId
     */
    public void cancelCourse(Integer courseId) {
        Query query = this.getCurrentSession().createQuery("update Course set selectNum=selectNum-1 where courseId=?");
        query.setInteger(0, courseId);
        query.executeUpdate();
    }

    /**
     * 根据id查找课程
     *
     * @param courseId
     * @return
     */
    public Course selectByCourseId(Integer courseId) {
        Course course = this.getCurrentSession().get(Course.class, courseId);
        return course;
    }

    /**
     * 选课
     *
     * @param courseId
     */
    public void confirmCourse(Integer courseId) {
        Query query = this.getCurrentSession().createQuery("update Course set selectNum=selectNum+1 where courseId=?");
        query.setInteger(0, courseId);
        query.executeUpdate();
    }

    /**
     * 返回课程数量，分页需要
     *
     * @return
     */
    public int findCount() {
        String hql = "select count(*) from Course";
        List<Long> list = this.getCurrentSession().createQuery(hql).list();
        if (list != null && list.size() > 0) {
            return list.get(0).intValue();
        }
        return 0;
    }

//    /**
//     * 根据id查询课程
//     * @param courseId
//     * @return
//     */
//    public Course getCourseById(Integer courseId) {
//       String hql="from Course where courseId=:n";
//       Query query=this.getCurrentSession().createQuery(hql);
//       query.setParameter("n",courseId);
//       Course course=(Course) query.uniqueResult();
//       return course;
//    }


    /**
     * 查询某个课程的先修课
     *
     * @param courseId
     * @return
     */
    public Course getPreviousCourse(Integer courseId) {
        Course course = this.getCurrentSession().get(Course.class, courseId);
        return course;
    }


    /**
     * 根据名称查找该课程记录有几条
     * 用作分页
     *
     * @param courseName
     * @return
     */
    public int findCountByName(String courseName) {
        String hql = "select count(courseId) from Course where courseName=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("n", courseName);
        List<Long> list = query.list();
        if (list != null && list.size() > 0) {
            return list.get(0).intValue();
        }
        return 0;
    }

    /**
     * 因为涉及到先行课，简化后，约定规定学分的为先行课
     *
     * @return
     */
    public List<Course> getAllPreviousCourse() {
        String hql = "from Course where credit=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("n", CreditConstant.previousCredit);
        List<Course> courseList = query.list();
        return courseList;
    }

    /**
     * 根据老师姓名查找记录的数量
     *
     * @param teacherName
     * @return
     */
    public int findCountByTeacher(String teacherName) {
        String hql = "select count(courseId) from Course where teacherName=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("n", teacherName);
        List<Long> list = query.list();
        if (list != null && list.size() > 0) {
            return list.get(0).intValue();
        }
        return 0;
    }

    /**
     * 根据给定的类别查找记录数量
     *
     * @param category
     * @return
     */
    public int findCountByCategory(Category category) {
        String hql = "select count(courseId) from Course where category.categoryId=:n";
        Query query = this.getCurrentSession().createQuery(hql);
        query.setParameter("n", category.getCategoryId());
        List<Long> list = query.list();
        if (list != null && list.size() > 0) {
            return list.get(0).intValue();
        }
        return 0;
    }

    public List<Course> getAllCourse() {
        String hql = "from Course";
        Query query = this.getCurrentSession().createQuery(hql);
        List<Course> courseList = query.list();
        return courseList;
    }
}
