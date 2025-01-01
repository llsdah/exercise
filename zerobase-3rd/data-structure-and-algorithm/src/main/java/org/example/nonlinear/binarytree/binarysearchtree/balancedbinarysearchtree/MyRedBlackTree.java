package org.example.nonlinear.binarytree.binarysearchtree.balancedbinarysearchtree;

public class MyRedBlackTree {
    /**
     * red black tree
     *
     * root 노드와 leaf 노드의 색은 black
     * red 노드의 자식은 black
     * 모든 leaf 노드에서 root 노드 까지의 가는 경로의 black 노드 수는 같은
     *
     * 조건이 깨지는 상황에서 ReBalancing
     *
     * 삽입
     * 노드 삽인 후 double red 발생
     * 부모 노드의 형제 노드가 red 일떄
     *
     * ReColoring 진행
     * 삽입한 노드의 부모와 부모의 형제 노드를 black으로 변경
     * 부모의 보모 노드를 red로 변경
     * 부모의 부모 노드가 root 인지 double red 인지에 따라 조정 진행
     *
     * 삽입
     * 노드 삽인 후 double red 발생
     * 부모 노드의 형제 노드가 block이거나 없을 때
     *
     * ReStructuring 진행
     * 조정 대상 : 삽입한 노드, 부모 노드, 부모의 부모 노드
     * 조정 대상 노드들을 오름 차순 정렬
     * 가운데 노드를 부모 노드로 선정하고 black 으로 변경
     * 나머지 두 노드를 자식 노드로 두고 red로 변경
     *
     * 삭제
     * 삭제 대상 노드가 black 이고 그 자리에 오는 노드가 red 인경우
     * 해당 자리로 오는 red 노드를 black으로 변경
     *
     * 삭제 1
     * 삭제 대상 노드가 black이고 그자리에 오는 노드가 black 인 경우
     * 이중 흑색 노드의 형제 노드가 black 이고, 형제의 양쪽 자식 모두 black 인 경우
     * 형제 노드를 red로 변경
     * 이중 흑색 노드의 검은색 1개를 부모 노드로 전달
     * 부모가 root가 아닌 이중 흑색 노드가 되면 해당 case 반복진행
     *
     * 삭제 2
     * 이중 흑색 노드의 형제 노드가 red인경우
     * 형제 노드를 black으로 변경
     * 부모 노드를 red로 변경
     * 부모 노드르 기준으로 왼쪽으로 회전
     * 다음 이중 흑색 case에 따라 반복 진행
     *
     * 삭제 3-1
     * 이중 흑색 노드의 형제 노드가 black이고,
     * 오른쪽 자식이 red인 경우
     * 부모노드와 형제 노드의 오른쪽 자식 노드를 검은색으로 변경
     * 부모 노드를 기준으로 왼쪽으로 회전
     *
     * 삭제 3-2
     * 이중 흑색 노드의 형제 노드가 black이고, 왼쪽 자식이 red인 경우
     * 형제 노드를 red로 변경
     * 형제 노드의 왼쪽 자식 노드를 black으로 변경
     * 형제 노드를 기준으로 오른쪽으로 회전
     * 이중 흑색 3-1 진행
     *
     */
}
