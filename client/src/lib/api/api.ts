import axios from 'axios';

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true,
});

export const getCoursesPublic = async (courseId: string) => {
  try {
    const { data } = await api.get(`/public/courses/${courseId}`);
    return data as CourseDetailsPublicDto;
  } catch (err) {
    console.log(err);
    throw new Error('Failed to fetch courses');
  }
};
