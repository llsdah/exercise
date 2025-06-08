package org.example.algorithm.minimunspanningtree;

public class MinimunSpanningTree {
    /**
     * 최소신장 트리
     * MST minimum spanning tree
     * 그래프상 모든 노드들을 최소 비용으로 연결하는 방법 - 크루스칼, 프림
     *
     * 크루스칼 알고리즘
     *  간전 중 최소 값을 가진 간선부터 연결
     *  사이클 발생 시 다른 간선 선택
     *      사이클 발생시 체크 방법 : Union-Find
     *      Union-Find
     *          최초 자기 자신 초기화, 계속 연결 시도해서 자기보다 작은 번호로 변경
     *          즉, 6과 5 연결시 5가 이미 2와 연결 되어 있는 경우 6은 2와 연결된 번호가 된다.
     *  주로 간선 수가 적을 때 사용
     *
     *
     * 프림
     *  임의 노드에서 시작
     *  연결된 노드들의 간선 중 낮은 가중치를 갖는 간선 선택
     *  간선의 개수가 많을 때 크루스칼보다 유리
     *
     */




}
