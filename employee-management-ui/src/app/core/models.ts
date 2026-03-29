export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface UserRecord {
  id: number;
  employeeId: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  contactNumber: string;
  location: string;
  profileImage?: string | null;
  dob?: string | null;
  age: number;
  managerId?: number | null;
  managerName?: string | null;
  role: Role;
  status: Status;
}

export interface ProjectRecord {
  id: number;
  name: string;
  description: string;
  status: 'ACTIVE' | 'COMPLETED';
  managerId?: number | null;
  managerName?: string | null;
}

export interface ProjectDetailRecord extends ProjectRecord {
  teamMembers: UserRecord[];
}

export interface TaskRecord {
  id: number;
  title: string;
  description: string;
  status: 'ASSIGNED' | 'COMPLETED' | 'APPROVED';
  projectId: number;
  projectName: string;
  managerId: number;
  managerName: string;
  employeeId: number;
  employeeName: string;
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface LoginRequest {
  identifier: string;
  password: string;
  role: Role;
}

export interface SignupRequest {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  dob: string;
  contactNumber: string;
  location: string;
  profileImage: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface UpdateProfileImageRequest {
  profileImage: string;
}

export interface UpdateProfileContactRequest {
  contactNumber: string;
}

export interface AssignEmployeeManagerRequest {
  employeeId: number;
  managerId: number;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
}

export interface AssignProjectManagerRequest {
  projectId: number;
  managerId: number;
}

export interface CreateTaskRequest {
  title: string;
  description: string;
  projectId: number;
  employeeId: number;
}

export interface UpdateTaskStatusRequest {
  status: 'ASSIGNED' | 'COMPLETED' | 'APPROVED';
}

export type Role = 'ADMIN' | 'MANAGER' | 'EMPLOYEE';
export type Status = 'PENDING' | 'APPROVED';
export type NoticeTone = 'success' | 'error' | 'info';
