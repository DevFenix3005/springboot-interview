export interface TaskResponse {
  id: number,
  title: string,
  priority: 'LOW' | 'MEDIUM' | 'HIGH',
  completed: boolean,
  createdAt: string,
}
